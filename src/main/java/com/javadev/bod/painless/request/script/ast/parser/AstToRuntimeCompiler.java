package com.javadev.bod.painless.request.script.ast.parser;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.script.ast.BinaryOperator;
import com.javadev.bod.painless.request.script.ast.commands.*;
import com.javadev.bod.painless.request.script.ast.conditions.BooleanExpressionCondition;
import com.javadev.bod.painless.request.script.ast.conditions.Condition;
import com.javadev.bod.painless.request.script.ast.conditions.EqualsCondition;
import com.javadev.bod.painless.request.script.ast.conditions.NotEqualsCondition;
import com.javadev.bod.painless.request.script.ast.expressions.*;
import com.javadev.bod.painless.request.script.ast.expressions.numeric.DecimalRuntimeExpression;
import com.javadev.bod.painless.request.script.ast.expressions.numeric.IntegerRuntimeExpression;
import com.javadev.bod.painless.request.script.ast.nodes.*;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.CastNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.ListInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapEntryNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.literal.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Compiler that transforms a parsed AST representation of a script into
 * an executable runtime command graph.
 *
 * <p>This component represents the second stage of the scripting pipeline:
 *
 * <pre>
 * script text
 *      ↓
 * tokenizer
 *      ↓
 * parser
 *      ↓
 * AST (ScriptNode tree)
 *      ↓
 * AstToRuntimeCompiler
 *      ↓
 * runtime command graph (Command / Expression objects)
 * </pre>
 *
 * <p>The compiler walks through the AST produced by the parser and converts
 * high-level syntax nodes ({@code StatementNode}, {@code ExpressionNode})
 * into executable runtime objects:
 *
 * <ul>
 *   <li>{@link Command} implementations for statements</li>
 *   <li>{@link Expression} implementations for expressions</li>
 *   <li>{@link Condition} implementations for conditional logic</li>
 * </ul>
 *
 * <h3>Compilation responsibilities</h3>
 *
 * <ul>
 *   <li>convert AST statement nodes into {@link Command} objects</li>
 *   <li>convert AST expression nodes into {@link Expression} objects</li>
 *   <li>transform compound assignments (e.g. {@code +=}) into binary expressions</li>
 *   <li>resolve assignment targets such as variables, paths and indexed maps</li>
 *   <li>construct executable control flow commands such as {@link IfCommand}</li>
 * </ul>
 *
 * <h3>Example</h3>
 *
 * Source script:
 *
 * <pre>
 * ctx._source.count += 1
 * </pre>
 *
 * Parsed AST:
 *
 * <pre>
 * ScriptNode
 * └── CompoundAssignmentNode (+=)
 *     ├── target
 *     │   └── PathReferenceNode(ctx._source.count)
 *     └── value
 *         └── IntegerLiteralNode(1)
 * </pre>
 *
 * Runtime representation produced by this compiler:
 *
 * <pre>
 * AssignPathCommand
 * ├── path: ctx._source.count
 * └── expression
 *     └── BinaryRuntimeExpression
 *         ├── left
 *         │   └── PathExpression(ctx._source.count)
 *         ├── right
 *         │   └── IntegerRuntimeExpression(1)
 *         └── operator: ADD
 * </pre>
 *
 * <h3>Example with control flow</h3>
 *
 * Script:
 *
 * <pre>
 * if (ctx._source.status == "READY") {
 *     return "ok";
 * }
 * </pre>
 *
 * Runtime structure:
 *
 * <pre>
 * BlockCommand
 * └── IfCommand
 *     ├── condition
 *     │   └── EqualsCondition
 *     │       ├── PathExpression(ctx._source.status)
 *     │       └── StringExpression("READY")
 *     ├── thenCommands
 *     │   └── ReturnWithValueCommand
 *     │       └── StringExpression("ok")
 *     └── elseCommands
 *         └── []
 * </pre>
 *
 * <h3>Design notes</h3>
 *
 * <ul>
 *   <li>This compiler does not execute the script. It only builds the runtime
 *       execution structure.</li>
 *
 *   <li>The resulting {@link Command} graph can later be executed against an
 *       {@link ExecutionContext ExecutionContext}.</li>
 *
 *   <li>Many AST constructs are simplified during compilation. For example
 *       compound assignments are rewritten into a combination of binary
 *       expressions and simple assignments.</li>
 *
 *   <li>Some AST nodes are lowered into specialized runtime expressions
 *       (e.g. update expressions for variables and paths).</li>
 * </ul>
 *
 * <h3>Thread safety</h3>
 *
 * <p>This compiler is stateless and therefore thread-safe.
 *
 */
public class AstToRuntimeCompiler {

    public Command compile(ScriptNode scriptNode) {
        List<Command> commands = new ArrayList<>();
        for (StatementNode statement : scriptNode.getStatements()) {
            commands.add(compileStatement(statement));
        }
        return new BlockCommand(commands);
    }

    private Command compileStatement(StatementNode node) {
        if (node instanceof BlockStatementNode blockNode) {
            return compileBlock(blockNode);
        }

        if (node instanceof IfStatementNode ifNode) {
            return compileIf(ifNode);
        }

        if (node instanceof ReturnStatementNode returnNode) {
            return compileReturn(returnNode);
        }

        if (node instanceof VariableDeclarationNode declarationNode) {
            return compileVariableDeclaration(declarationNode);
        }

        if (node instanceof AssignmentStatementNode assignmentNode) {
            return compileAssignment(assignmentNode);
        }

        if (node instanceof CompoundAssignmentNode compoundAssignmentNode) {
            return compileCompoundAssignment(compoundAssignmentNode);
        }

        throw new IllegalArgumentException("Unsupported statement node: " + node.getClass().getName());
    }

    private Command compileBlock(BlockStatementNode blockNode) {
        List<Command> commands = new ArrayList<>();
        for (StatementNode statement : blockNode.getStatements()) {
            commands.add(compileStatement(statement));
        }
        return new BlockCommand(commands);
    }

    private Command compileIf(IfStatementNode ifNode) {
        Condition condition = compileCondition(ifNode.getCondition());

        List<Command> thenCommands = toCommandList(ifNode.getThenBranch());
        List<Command> elseCommands = ifNode.getElseBranch() == null
                ? List.of()
                : toCommandList(ifNode.getElseBranch());

        return new IfCommand(condition, thenCommands, elseCommands);
    }

    private List<Command> toCommandList(StatementNode statementNode) {
        if (statementNode instanceof BlockStatementNode blockNode) {
            List<Command> commands = new ArrayList<>();
            for (StatementNode statement : blockNode.getStatements()) {
                commands.add(compileStatement(statement));
            }
            return commands;
        }
        return List.of(compileStatement(statementNode));
    }

    private Command compileReturn(ReturnStatementNode returnNode) {
        if (returnNode.getExpression() == null) {
            return new ReturnCommand();
        }

        return new ReturnWithValueCommand(compileExpression(returnNode.getExpression()));
    }

    private Command compileVariableDeclaration(VariableDeclarationNode declarationNode) {
        String declaredType = declarationNode.getDeclaredType();
        String variableName = declarationNode.getVariableName();
        ExpressionNode initializer = declarationNode.getInitializer();

        if ("Map".equals(declaredType)
                && (initializer instanceof EmptyMapLiteralNode || initializer instanceof MapInitializationNode)) {
            return new AssignVariableCommand(variableName, compileExpression(initializer));
        }

        return new AssignVariableCommand(variableName, compileExpression(initializer));
    }

    private Command compileAssignment(AssignmentStatementNode assignmentNode) {
        AssignableNode target = assignmentNode.getTarget();
        Expression expression = compileExpression(assignmentNode.getValue());

        if (target instanceof VariableTargetNode variableTargetNode) {
            return new AssignVariableCommand(variableTargetNode.getVariableName(), expression);
        }

        if (target instanceof PathTargetNode pathTargetNode) {
            return new AssignPathCommand(pathTargetNode.getPath(), expression);
        }

        if (target instanceof IndexTargetNode indexTargetNode) {
            return compileIndexAssignment(indexTargetNode, expression);
        }

        throw new IllegalArgumentException("Unsupported assignment target: " + target.getClass().getName());
    }

    private Command compileCompoundAssignment(CompoundAssignmentNode node) {
        AssignableNode target = node.getTarget();
        Expression right = compileExpression(node.getValue());

        if (target instanceof VariableTargetNode variableTargetNode) {
            Expression left = new VariableExpression(variableTargetNode.getVariableName());
            Expression combined = new BinaryRuntimeExpression(left, right, toBinaryOperator(node.getOperator()));
            return new AssignVariableCommand(variableTargetNode.getVariableName(), combined);
        }

        if (target instanceof PathTargetNode pathTargetNode) {
            Expression left = new PathExpression(pathTargetNode.getPath());
            Expression combined = new BinaryRuntimeExpression(left, right, toBinaryOperator(node.getOperator()));
            return new AssignPathCommand(pathTargetNode.getPath(), combined);
        }

        throw new IllegalArgumentException("Unsupported compound assignment target: " + target.getClass().getName());
    }

    private BinaryOperator toBinaryOperator(CompoundAssignmentOperator operator) {
        return switch (operator) {
            case ADD_ASSIGN -> BinaryOperator.ADD;
            case SUB_ASSIGN -> BinaryOperator.SUB;
            case MUL_ASSIGN -> BinaryOperator.MUL;
            case DIV_ASSIGN -> BinaryOperator.DIV;
            case MOD_ASSIGN -> BinaryOperator.MOD;
        };
    }

    private Command compileIndexAssignment(IndexTargetNode targetNode, Expression expression) {
        if (targetNode.getIndex() instanceof StringLiteralNode stringLiteralNode) {
            PathRef path = tryResolvePathTarget(targetNode.getTarget(), stringLiteralNode.getValue());
            if (path != null) {
                return new AssignPathCommand(path, expression);
            }
        }

        throw new IllegalArgumentException("Only string-key map index assignment is supported");
    }

    private PathRef tryResolvePathTarget(ExpressionNode baseTarget, String indexKey) {
        if (baseTarget instanceof PathReferenceNode pathReferenceNode) {
            List<String> segments = new ArrayList<>(pathReferenceNode.getPath().segments());
            segments.add(indexKey);
            return new PathRef(segments);
        }

        if (baseTarget instanceof VariableReferenceNode variableReferenceNode) {
            List<String> segments = new ArrayList<>();
            segments.add(variableReferenceNode.getVariableName());
            segments.add(indexKey);
            return new PathRef(segments);
        }

        return null;
    }

    private Condition compileCondition(ExpressionNode conditionExpression) {
        if (conditionExpression instanceof BinaryExpressionNode binaryNode) {
            Expression left = compileExpression(binaryNode.getLeft());
            Expression right = compileExpression(binaryNode.getRight());

            if (binaryNode.getOperator() == BinaryOperator.EQ) {
                return new EqualsCondition(left, right);
            }

            if (binaryNode.getOperator() == BinaryOperator.NE) {
                return new NotEqualsCondition(left, right);
            }
        }

        return new BooleanExpressionCondition(compileExpression(conditionExpression));
    }

    private Expression compileExpression(ExpressionNode node) {
        if (node instanceof NullLiteralNode) {
            return new NullExpression();
        }

        if (node instanceof StringLiteralNode stringLiteralNode) {
            return new StringExpression(stringLiteralNode.getValue());
        }

        if (node instanceof IntegerLiteralNode integerLiteralNode) {
            return new IntegerRuntimeExpression(integerLiteralNode.getValue());
        }

        if (node instanceof BooleanLiteralNode n) {
            return new BooleanRuntimeExpression(n.getValue());
        }

        if (node instanceof DecimalLiteralNode decimalLiteralNode) {
            return new DecimalRuntimeExpression(decimalLiteralNode.getValue());
        }

        if (node instanceof VariableReferenceNode variableReferenceNode) {
            return new VariableExpression(variableReferenceNode.getVariableName());
        }

        if (node instanceof PathReferenceNode pathReferenceNode) {
            return new PathExpression(pathReferenceNode.getPath());
        }

        if (node instanceof BinaryExpressionNode binaryExpressionNode) {
            return new BinaryRuntimeExpression(
                    compileExpression(binaryExpressionNode.getLeft()),
                    compileExpression(binaryExpressionNode.getRight()),
                    binaryExpressionNode.getOperator()
            );
        }

        if (node instanceof IndexAccessNode indexAccessNode) {
            return new IndexAccessExpression(
                    compileExpression(indexAccessNode.getTarget()),
                    compileExpression(indexAccessNode.getIndex())
            );
        }

        if (node instanceof FunctionCallNode functionCallNode) {
            List<Expression> args = new ArrayList<>();
            for (ExpressionNode argNode : functionCallNode.getArguments()) {
                args.add(compileExpression(argNode));
            }
            return new FunctionCallExpression(functionCallNode.getFunctionName(), args);
        }

        if (node instanceof EmptyMapLiteralNode) {
            return new NewLinkedHashMapExpression();
        }

        if (node instanceof ListInitializationNode listNode) {
            List<Expression> elements = new ArrayList<>();
            for (ExpressionNode elementNode : listNode.getElements()) {
                elements.add(compileExpression(elementNode));
            }
            return new ListInitializationExpression(elements);
        }

        if (node instanceof MapInitializationNode mapNode) {
            List<MapEntryExpression> entries = new ArrayList<>();
            for (MapEntryNode entryNode : mapNode.getEntries()) {
                entries.add(new MapEntryExpression(
                        compileExpression(entryNode.getKey()),
                        compileExpression(entryNode.getValue())
                ));
            }
            return new MapInitializationExpression(entries);
        }

        if (node instanceof PropertyAccessNode propertyAccessNode) {
            return new PropertyAccessExpression(
                    compileExpression(propertyAccessNode.getTarget()),
                    propertyAccessNode.getPropertyName()
            );
        }

        if (node instanceof CastNode castNode) {
            return new CastExpression(
                    castNode.getTargetType(),
                    compileExpression(castNode.getExpression())
            );
        }

        if (node instanceof InstanceOfNode instanceOfNode) {
            return new InstanceOfExpression(
                    compileExpression(instanceOfNode.getExpression()),
                    instanceOfNode.getTypeName()
            );
        }

        if (node instanceof MethodCallNode n) {
            Expression target = compileExpression(n.getTarget());

            List<Expression> args = n.getArguments()
                    .stream()
                    .map(this::compileExpression)
                    .toList();

            return new MethodCallExpression(target, n.getMethodName(), args);
        }

        if (node instanceof UpdateExpressionNode updateNode) {
            return compileUpdateExpression(updateNode);
        }

        if (node instanceof UnaryExpressionNode unaryNode) {
            return new UnaryRuntimeExpression(
                    unaryNode.getOperator(),
                    compileExpression(unaryNode.getOperand())
            );
        }


        throw new IllegalArgumentException("Unsupported expression node: " + node.getClass().getName());
    }

    private Expression compileUpdateExpression(UpdateExpressionNode updateNode) {
        ExpressionNode targetNode = updateNode.getTarget();

        if (targetNode instanceof VariableReferenceNode variableReferenceNode) {
            return new VariableUpdateExpression(
                    variableReferenceNode.getVariableName(),
                    updateNode.getOperator(),
                    updateNode.isPrefix()
            );
        }

        if (targetNode instanceof PathReferenceNode pathReferenceNode) {
            return new PathUpdateExpression(
                    pathReferenceNode.getPath(),
                    updateNode.getOperator(),
                    updateNode.isPrefix()
            );
        }

        throw new IllegalArgumentException("Unsupported update target: " + targetNode.getClass().getName());
    }
}
