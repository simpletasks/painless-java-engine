package com.javadev.bod.painless.request.script.ast;

import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.script.ast.nodes.*;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.CastNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.ListInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapEntryNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.literal.*;

public class AstPrinterVisitor implements AstVisitor<String> {

    private int indent = 0;

    private String line(String text) {
        return "  ".repeat(indent) + text + System.lineSeparator();
    }

    private String visitChild(String label, AssignableNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(label + ":"));
        indent++;
        sb.append(node.accept(this));
        indent--;
        return sb.toString();
    }

    private String visitChild(String label, StatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(label + ":"));
        indent++;
        sb.append(node.accept(this));
        indent--;
        return sb.toString();
    }

    private String visitChild(String label, ExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(label + ":"));
        indent++;
        sb.append(node.accept(this));
        indent--;
        return sb.toString();
    }

    private String visitNullableChild(String label, StatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line(label + ":"));
        indent++;
        if (node == null) {
            sb.append(line("null"));
        } else {
            sb.append(node.accept(this));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitScript(ScriptNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("ScriptNode"));
        indent++;
        for (StatementNode statement : node.getStatements()) {
            sb.append(statement.accept(this));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitBlockStatement(BlockStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("BlockStatementNode"));
        indent++;
        for (StatementNode statement : node.getStatements()) {
            sb.append(statement.accept(this));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitIfStatement(IfStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("IfStatementNode"));
        indent++;
        sb.append(visitChild("condition", node.getCondition()));
        sb.append(visitChild("thenBranch", node.getThenBranch()));
        sb.append(visitNullableChild("elseBranch", node.getElseBranch()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitReturnStatement(ReturnStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("ReturnStatementNode"));
        indent++;
        if (node.getExpression() == null) {
            sb.append(line("expression: null"));
        } else {
            sb.append(visitChild("expression", node.getExpression()));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitAssignmentStatement(AssignmentStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("AssignmentStatementNode"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        sb.append(visitChild("value", node.getValue()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitCompoundAssignmentStatement(CompoundAssignmentNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("CompoundAssignmentNode(" + node.getOperator() + ")"));
        indent++;
        sb.append(visitChild("target", (ExpressionNode) node.getTarget()));
        sb.append(visitChild("value", node.getValue()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitVariableDeclaration(VariableDeclarationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("VariableDeclarationNode"));
        indent++;
        sb.append(line("declaredType: " + node.getDeclaredType()));
        sb.append(line("variableName: " + node.getVariableName()));
        sb.append(visitChild("initializer", node.getInitializer()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitExpressionStatement(ExpressionStatementNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("ExpressionStatementNode"));
        indent++;
        sb.append(visitChild("expression", node.getExpression()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitVariableTarget(VariableTargetNode node) {
        return line("VariableTargetNode(" + node.getVariableName() + ")");
    }

    @Override
    public String visitPathTarget(PathTargetNode node) {
        return line("PathTargetNode(" + node.getPath().segments() + ")");
    }

    @Override
    public String visitIndexTarget(IndexTargetNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("IndexTargetNode"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        sb.append(visitChild("index", node.getIndex()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitNullLiteral(NullLiteralNode node) {
        return line("NullLiteralNode");
    }

    @Override
    public String visitBooleanLiteral(BooleanLiteralNode node) {
        return line("BooleanLiteralNode(" + node.getValue() + ")");
    }

    @Override
    public String visitStringLiteral(StringLiteralNode node) {
        return line("StringLiteralNode(\"" + node.getValue() + "\")");
    }

    @Override
    public String visitNumericalLiteral(NumericLiteralNode node) {
        return line("NumericLiteralNode(\"" + node.getValue() + "\")");
    }

    @Override
    public String visitLiteral(LiteralExpressionNode node) {
        return line("LiteralExpressionNode(\"" + "__EMPTY__" + "\")");
    }

    @Override
    public String visitIntegerLiteral(IntegerLiteralNode node) {
        return line("IntegerLiteralNode(" + node.getValue() + ")");
    }

    @Override
    public String visitDecimalLiteral(DecimalLiteralNode node) {
        return line("DecimalLiteralNode(" + node.getValue() + ")");
    }

    @Override
    public String visitEmptyMapLiteral(EmptyMapLiteralNode node) {
        return line("EmptyMapLiteralNode([:])");
    }

    @Override
    public String visitVariableReference(VariableReferenceNode node) {
        return line("VariableReferenceNode(" + node.getVariableName() + ")");
    }

    @Override
    public String visitPathReference(PathReferenceNode node) {
        return line("PathReferenceNode(" + node.getPath().segments() + ")");
    }

    @Override
    public String visitIndexAccess(IndexAccessNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("IndexAccessNode"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        sb.append(visitChild("index", node.getIndex()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitPropertyAccess(PropertyAccessNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("PropertyAccessNode"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        sb.append(line("propertyName: " + node.getPropertyName()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitFunctionCall(FunctionCallNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("FunctionCallNode(" + node.getFunctionName() + ")"));
        indent++;
        for (ExpressionNode argument : node.getArguments()) {
            sb.append(visitChild("arg", argument));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitMethodCall(MethodCallNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("MethodCallNode(" + node.getMethodName() + ")"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        for (ExpressionNode argument : node.getArguments()) {
            sb.append(visitChild("arg", argument));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitBinaryExpression(BinaryExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("BinaryExpressionNode(" + node.getOperator() + ")"));
        indent++;
        sb.append(visitChild("left", node.getLeft()));
        sb.append(visitChild("right", node.getRight()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitUnaryExpression(UnaryExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("UnaryExpressionNode(" + node.getOperator() + ")"));
        indent++;
        sb.append(visitChild("operand", node.getOperand()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitCast(CastNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("CastNode(" + node.getTargetType() + ")"));
        indent++;
        sb.append(visitChild("expression", node.getExpression()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitInstanceOf(InstanceOfNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("InstanceOfNode(" + node.getTypeName() + ")"));
        indent++;
        sb.append(visitChild("expression", node.getExpression()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitUpdateExpression(UpdateExpressionNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("UpdateExpressionNode(" + node.getOperator() + ", prefix=" + node.isPrefix() + ")"));
        indent++;
        sb.append(visitChild("target", node.getTarget()));
        indent--;
        return sb.toString();
    }

    @Override
    public String visitListInitialization(ListInitializationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("ListInitializationNode"));
        indent++;
        for (ExpressionNode element : node.getElements()) {
            sb.append(visitChild("element", element));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitMapInitialization(MapInitializationNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("MapInitializationNode"));
        indent++;
        for (MapEntryNode entry : node.getEntries()) {
            sb.append(entry.accept(this));
        }
        indent--;
        return sb.toString();
    }

    @Override
    public String visitMapEntry(MapEntryNode node) {
        StringBuilder sb = new StringBuilder();
        sb.append(line("MapEntryNode"));
        indent++;
        sb.append(visitChild("key", node.getKey()));
        sb.append(visitChild("value", node.getValue()));
        indent--;
        return sb.toString();
    }

}