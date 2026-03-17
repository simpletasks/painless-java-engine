package com.javadev.bod.painless.request.script.ast.parser;

import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.ScriptNode;
import com.javadev.bod.painless.request.script.ast.BinaryOperator;
import com.javadev.bod.painless.request.script.ast.nodes.*;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.CastNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.ListInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapEntryNode;
import com.javadev.bod.painless.request.script.ast.nodes.initialization.MapInitializationNode;
import com.javadev.bod.painless.request.script.ast.nodes.literal.*;
import com.javadev.bod.painless.request.script.ast.tokenizer.Token;
import com.javadev.bod.painless.request.script.ast.tokenizer.TokenType;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * <pre>{@code
 *
 * expression
 *     → equality
 *
 * equality
 *     → primary ( ( "==" | "!=" ) primary )*
 *
 * primary
 *     → IDENTIFIER
 *     | STRING
 *     | NULL
 *     | functionCall
 *     | bracketInit
 *
 *
 * }</pre>
 * Primary expression is basic component
 *
 * <pre>{@code
 *
 * parseExpression
 *    ↓
 * parseEquality        (==, !=)
 *    ↓
 * parseAdditive        (+, -)
 *    ↓
 * parseMultiplicative  (*, /)
 *    ↓
 * parseUnary           (!, -, ++)
 *    ↓
 * parsePrimaryExpression
 *
 * }</pre>
 *
 * <pre>{@code
 *
 * ctx.status == 'READY'
 *
 * BinaryExpression (==)
 *  ├─ left
 *  │    └─ PathReferenceNode(ctx.status)
 *  │
 *  └─ right
 *       └─ StringLiteralNode("READY")
 *
 * }</pre>
 *
 * <p>Extended note:</p>
 *
 * <p>The parser now resolves binary expressions using operator precedence
 * instead of separate parseEquality / parseAdditive / parseMultiplicative methods.
 * The conceptual order remains the same, but precedence is now driven by
 * {@link BinaryOperator#getPrecedence()} and parsed through
 * {@code parseBinaryExpression(int minPrecedence)}.</p>
 *
 * <p>This allows the parser to support expressions such as:</p>
 *
 * <pre>{@code
 * a + b * c
 * ctx.status == 'READY' && user != null
 * obj['x'] != null || ctx.state == 'DONE'
 * }</pre>
 *
 * <p>Grouped expressions still use {@code ( ... )}, while square brackets
 * remain reserved for list/map initialization and index access.</p>
 */
@RequiredArgsConstructor
public class PainlessSubsetAstParser {
    private final List<Token> tokens;
    private int pos = 0;

    public ScriptNode parseScript() {
        List<StatementNode> statements = new ArrayList<>();

        while (!is(TokenType.EOF)) {
            if (is(TokenType.SEMICOLON)) {
                consume();
                continue;
            }
            statements.add(parseStatement());
        }

        return new ScriptNode(statements);
    }

    private StatementNode parseStatement() {
        if (match(TokenType.IF)) {
            return parseIfStatement();
        }

        if (match(TokenType.RETURN)) {
            return parseReturnStatement();
        }

        if (isTypedVariableDeclaration()) {
            return parseTypedVariableDeclaration();
        }

        if (is(TokenType.LBRACE)) {
            return parseBlockStatement();
        }

        return parseAssignmentStatement();
    }

    private StatementNode parseReturnStatement() {
        if (is(TokenType.SEMICOLON)) {
            consume();
            return new ReturnStatementNode(null);
        }

        if (isStatementTerminator()) {
            return new ReturnStatementNode(null);
        }

        ExpressionNode expression = parseExpression();
        optionalSemicolon();
        return new ReturnStatementNode(expression);
    }

    private boolean isStatementTerminator() {
        return is(TokenType.RBRACE)
                || is(TokenType.EOF)
                || is(TokenType.ELSE);
    }

    private boolean isTypedVariableDeclaration() {
        return is(TokenType.IDENTIFIER)
                && lookahead(1).getType() == TokenType.IDENTIFIER
                && lookahead(2).getType() == TokenType.ASSIGN;
    }

    private StatementNode parseTypedVariableDeclaration() {
        Token declaredType = expect(TokenType.IDENTIFIER);
        Token variable = expect(TokenType.IDENTIFIER);
        expect(TokenType.ASSIGN);

        ExpressionNode initializer = parseExpression();
        optionalSemicolon();

        return new VariableDeclarationNode(
                declaredType.getText(),
                variable.getText(),
                initializer
        );
    }

    /**
     * <pre>{@code
     *
     * if (ctx.status == 'READY') {
     *     return 'ok';
     * }
     *
     * ScriptNode
     *  └─ IfNode
     *       ├─ condition
     *       │    └─ BinaryExpressionNode (==)
     *       │          ├─ PathReferenceNode(ctx.status)
     *       │          └─ StringLiteralNode("READY")
     *       │
     *       └─ thenBlock
     *            └─ ReturnNode
     *                 └─ StringLiteralNode("ok")
     * }</pre>
     *
     * @return
     */
    private StatementNode parseIfStatement() {
        expect(TokenType.LPAREN);
        ExpressionNode condition = parseExpression();
        expect(TokenType.RPAREN);

        StatementNode thenBranch = parseStatementOrBlockNode();
        StatementNode elseBranch = null;

        if (match(TokenType.ELSE)) {
            elseBranch = parseStatementOrBlockNode();
        }

        return new IfStatementNode(condition, thenBranch, elseBranch);
    }

    private StatementNode parseStatementOrBlockNode() {
        if (is(TokenType.LBRACE)) {
            return parseBlockStatement();
        }
        return parseStatement();
    }

    private BlockStatementNode parseBlockStatement() {
        expect(TokenType.LBRACE);

        List<StatementNode> statements = new ArrayList<>();
        while (!is(TokenType.RBRACE)) {
            if (is(TokenType.SEMICOLON)) {
                consume();
                continue;
            }
            statements.add(parseStatement());
        }

        expect(TokenType.RBRACE);
        return new BlockStatementNode(statements);
    }

    private StatementNode parseAssignmentStatement() {
        AssignableNode target = parseAssignmentTarget();
        expect(TokenType.ASSIGN);

        ExpressionNode value = parseExpression();
        optionalSemicolon();

        return new AssignmentStatementNode(target, value);
    }

    private AssignableNode parseAssignmentTarget() {
        Token first = expect(TokenType.IDENTIFIER);
        String root = first.getText();

        List<String> segments = new ArrayList<>();
        segments.add(root);

        while (match(TokenType.DOT)) {
            segments.add(expect(TokenType.IDENTIFIER).getText());
        }

        if (match(TokenType.LBRACKET)) {
            ExpressionNode index = parseExpression();
            expect(TokenType.RBRACKET);

            ExpressionNode baseExpr;
            if (segments.size() == 1 && !"ctx".equals(root)) {
                baseExpr = new VariableReferenceNode(root);
            } else {
                baseExpr = new PathReferenceNode(new PathRef(segments));
            }

            return new IndexTargetNode(baseExpr, index);
        }

        if (segments.size() == 1) {
            return new VariableTargetNode(root);
        }

        return new PathTargetNode(new PathRef(segments));
    }

    private ExpressionNode parseExpression() {
        ExpressionNode expression = parseBinaryExpression(1);

        while (match(TokenType.INSTANCEOF)) {
            String typeName = expect(TokenType.IDENTIFIER).getText();
            expression = new InstanceOfNode(expression, typeName);
        }

        return expression;
    }

    private ExpressionNode parseBinaryExpression(int minPrecedence) {
        ExpressionNode left = parseUnaryExpression();

        while (true) {
            BinaryOperator operator = peekBinaryOperator();
            if (operator == null || operator.getPrecedence() < minPrecedence) {
                break;
            }

            consume();

            ExpressionNode right = parseBinaryExpression(operator.getPrecedence() + 1);
            left = new BinaryExpressionNode(left, operator, right);
        }

        return left;
    }

    private BinaryOperator peekBinaryOperator() {
        TokenType type = peek().getType();

        return switch (type) {
            case OR_OR -> BinaryOperator.OR;
            case AND_AND -> BinaryOperator.AND;

            case EQEQ -> BinaryOperator.EQ;
            case NE -> BinaryOperator.NE;

            case GT -> BinaryOperator.GT;
            case GTE -> BinaryOperator.GTE;
            case LT -> BinaryOperator.LT;
            case LTE -> BinaryOperator.LTE;

            case PLUS -> BinaryOperator.ADD;
            case MINUS -> BinaryOperator.SUB;

            case STAR -> BinaryOperator.MUL;
            case SLASH -> BinaryOperator.DIV;
            case PERCENT -> BinaryOperator.MOD;

            default -> null;
        };
    }

    private ExpressionNode parsePostfixExpression(ExpressionNode expr) {
        while (true) {
            if (match(TokenType.LBRACKET)) {
                ExpressionNode index = parseExpression();
                expect(TokenType.RBRACKET);
                expr = new IndexAccessNode(expr, index);
                continue;
            }

            if (match(TokenType.DOT)) {
                Token member = expect(TokenType.IDENTIFIER);

                if (match(TokenType.LPAREN)) {
                    List<ExpressionNode> args = new ArrayList<>();

                    if (!is(TokenType.RPAREN)) {
                        args.add(parseExpression());
                        while (match(TokenType.COMMA)) {
                            args.add(parseExpression());
                        }
                    }

                    expect(TokenType.RPAREN);
                    expr = new MethodCallNode(expr, member.getText(), args);
                    continue;
                }

                List<String> segments = tryExtendPath(expr, member.getText());
                if (segments != null) {
                    expr = new PathReferenceNode(new PathRef(segments));
                    continue;
                }

                expr = new PropertyAccessNode(expr, member.getText());
                continue;
            }

            if (match(TokenType.PLUS_PLUS)) {
                expr = new UpdateExpressionNode(expr, UpdateOperator.INCREMENT, false);
                continue;
            }

            if (match(TokenType.MINUS_MINUS)) {
                expr = new UpdateExpressionNode(expr, UpdateOperator.DECREMENT, false);
                continue;
            }

            break;
        }
        return expr;
    }

    /**
     * Unary operators
     * <pre>{@code
     * !flag
     * -a
     * +amount
     * !(a > b)
     * -(x + y)
     *
     *
     * }</pre>
     * @return
     */
    private ExpressionNode parseUnaryExpression() {
        if (match(TokenType.PLUS_PLUS)) {
            ExpressionNode target = parseUnaryExpression();
            return new UpdateExpressionNode(target, UpdateOperator.INCREMENT, true);
        }

        if (match(TokenType.MINUS_MINUS)) {
            ExpressionNode target = parseUnaryExpression();
            return new UpdateExpressionNode(target, UpdateOperator.DECREMENT, true);
        }

        if (match(TokenType.BANG)) {
            ExpressionNode operand = parseUnaryExpression();
            return new UnaryExpressionNode(UnaryOperator.NOT, operand);
        }

        if (match(TokenType.PLUS)) {
            ExpressionNode operand = parseUnaryExpression();
            return new UnaryExpressionNode(UnaryOperator.PLUS, operand);
        }

        if (match(TokenType.MINUS)) {
            ExpressionNode operand = parseUnaryExpression();
            return new UnaryExpressionNode(UnaryOperator.MINUS, operand);
        }

        if (match(TokenType.LPAREN)) {
            if (is(TokenType.IDENTIFIER) && lookAhead(1).getType() == TokenType.RPAREN) {
                String targetType = consume().getText();
                expect(TokenType.RPAREN);

                ExpressionNode expression = parseUnaryExpression();
                return new CastNode(targetType, expression);
            }

            ExpressionNode expression = parseExpression();
            expect(TokenType.RPAREN);
            return parsePostfixExpression(expression);
        }

        ExpressionNode expr = parsePrimaryExpression();
        return parsePostfixExpression(expr);
    }

    private Token lookAhead(int offset) {
        int target = pos + offset;
        if (target >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(target);
    }

    private List<String> tryExtendPath(ExpressionNode expr, String segment) {
        if (expr instanceof PathReferenceNode pathReferenceNode) {
            List<String> segments = new ArrayList<>(pathReferenceNode.getPath().segments());
            segments.add(segment);
            return segments;
        }

        if (expr instanceof VariableReferenceNode variableReferenceNode) {
            List<String> segments = new ArrayList<>();
            segments.add(variableReferenceNode.getVariableName());
            segments.add(segment);
            return segments;
        }

        return null;
    }

    /**
     * Parses a primary expression inside a larger statement.
     *
     * <p>This method handles the smallest units of expressions such as literals,
     * variable references, function calls and bracket initializations.
     * It is typically invoked while parsing a larger statement or expression.</p>
     *
     * <h2>Example 1 — null literal</h2>
     * <p>
     * Full statement:
     * <pre>
     * if (status == null) {
     *     return 'missing';
     * }
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * null
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * NullLiteralNode
     * </pre>
     *
     * <h2>Example 2 — string literal</h2>
     * <p>
     * Full statement:
     * <pre>
     * if (ctx.status == 'READY') {
     *     return 'ok';
     * }
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * 'READY'
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * StringLiteralNode("READY")
     * </pre>
     *
     * <h2>Example 3 — variable reference</h2>
     * <p>
     * Full statement:
     * <pre>
     * status = computeStatus(user)
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * status
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * VariableReferenceNode("status")
     * </pre>
     *
     * <h2>Example 4 — context reference</h2>
     * <p>
     * Full statement:
     * <pre>
     * if (ctx == null) {
     *     return 'missing';
     * }
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * ctx
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * PathReferenceNode
     *  └─ ctx
     * </pre>
     *
     * <h2>Example 5 — function call</h2>
     * <p>
     * Full statement:
     * <pre>
     * status = resolveStatus('READY', ctx.user)
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * resolveStatus('READY', ctx.user)
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * FunctionCallNode("resolveStatus")
     *  ├─ StringLiteralNode("READY")
     *  └─ PathReferenceNode(ctx.user)
     * </pre>
     *
     * <h2>Example 6 — bracket initialization</h2>
     * <p>
     * Full statement:
     * <pre>
     * values = ['a', 'b', 'c']
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * ['a', 'b', 'c']
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     * BracketInitializationNode
     *  ├─ StringLiteralNode("a")
     *  ├─ StringLiteralNode("b")
     *  └─ StringLiteralNode("c")
     * </pre>
     * <p>
     * Expression parsed here:
     * <pre>
     * a = 5
     * b = 3.14
     * </pre>
     * <p>
     * AST fragment produced:
     * <pre>
     *  ScriptNode
     *  ├─ AssignmentNode
     *  │   ├─ VariableTargetNode(a)
     *  │   └─ IntegerLiteralNode(5)
     *  │
     *  └─ AssignmentNode
     *      ├─ VariableTargetNode(b)
     *      └─ DecimalLiteralNode(3.14)
     * </pre>
     *
     * <p>Extended note:</p>
     *
     * <p>After the primary expression is parsed, postfix parsing may extend it into:</p>
     *
     * <pre>
     * base[index]
     * base.property
     * base.method(arg1, arg2)
     * expr++
     * expr--
     * </pre>
     *
     * <p>Grouping with parentheses is not parsed here directly. It is handled in
     * {@code parseUnaryExpression()}, because grouped expressions and casts both
     * start with {@code (}.</p>
     *
     * @return parsed {@link ExpressionNode}
     * @throws IllegalArgumentException if the current token cannot start a valid primary expression
     */
    private ExpressionNode parsePrimaryExpression() {
        if (match(TokenType.NULL)) {
            return new NullLiteralNode();
        }

        if (is(TokenType.STRING)) {
            return new StringLiteralNode(consume().getText());
        }

        if (is(TokenType.INTEGER)) {
            return new IntegerLiteralNode(Long.parseLong(consume().getText()));
        }

        if (is(TokenType.DECIMAL)) {
            return new DecimalLiteralNode(new BigDecimal(consume().getText()));
        }

        if (match(TokenType.TRUE)) {
            return new BooleanLiteralNode(true);
        }

        if (match(TokenType.FALSE)) {
            return new BooleanLiteralNode(false);
        }

        if (match(TokenType.LBRACKET)) {
            return parseBracketInitialization();
        }

        if (is(TokenType.IDENTIFIER)) {
            Token id = consume();

            if (match(TokenType.LPAREN)) {
                List<ExpressionNode> args = new ArrayList<>();

                if (!is(TokenType.RPAREN)) {
                    args.add(parseExpression());
                    while (match(TokenType.COMMA)) {
                        args.add(parseExpression());
                    }
                }

                expect(TokenType.RPAREN);
                return new FunctionCallNode(id.getText(), args);
            }

            if ("ctx".equals(id.getText())) {
                return new PathReferenceNode(new PathRef(List.of("ctx")));
            }

            return new VariableReferenceNode(id.getText());
        }

        throw error("Unsupported expression");
    }

    /**
     * 'Map' initialization, Array initialization
     *
     * @return
     */
    private ExpressionNode parseBracketInitialization() {

        // [:]
//        if (match(TokenType.COLON)) {
//            expect(TokenType.RBRACKET);
//            return new MapInitializationNode(new ArrayList<>());
//        }
        if (match(TokenType.COLON_RBRACKET)) {
            return new MapInitializationNode(new ArrayList<>());
        }

        // []
        if (match(TokenType.RBRACKET)) {
            return new ListInitializationNode(new ArrayList<>());
        }

        ExpressionNode first = parseExpression();

        // MapEntry array [ "key": value, "key" : value, ...]
        if (match(TokenType.COLON)) {
            ExpressionNode firstValue = parseExpression();

            List<MapEntryNode> entries = new ArrayList<>();
            entries.add(new MapEntryNode(first, firstValue));

            while (match(TokenType.COMMA)) {
                ExpressionNode key = parseExpression();
                expect(TokenType.COLON);
                ExpressionNode value = parseExpression();
                entries.add(new MapEntryNode(key, value));
            }

            expect(TokenType.RBRACKET);
            return new MapInitializationNode(entries);
        }

        // array [expr,expr,expr,expr, ..]
        List<ExpressionNode> elements = new ArrayList<>();
        elements.add(first);

        while (match(TokenType.COMMA)) {
            elements.add(parseExpression());
        }

        expect(TokenType.RBRACKET);
        return new ListInitializationNode(elements);
    }

    private void optionalSemicolon() {
        if (is(TokenType.SEMICOLON)) {
            consume();
        }
    }

    /**
     * Read current token - does not move position
     * Conditional test
     *
     * @param type
     * @return
     */
    private boolean is(TokenType type) {
        return peek().getType() == type;
    }

    private boolean match(TokenType type) {
        if (is(type)) {
            pos++;
            return true;
        }
        return false;
    }

    /**
     * Must be expected token otherwise throw
     *
     * @param type
     * @return
     */
    private Token expect(TokenType type) {
        Token t = peek();
        if (t.getType() != type) {
            throw error("Expected " + type + " but got " + t.getType() + " [" + t.getText() + "]");
        }
        pos++;
        return t;
    }

    private Token consume() {
        return tokens.get(pos++);
    }

    private Token peek() {
        return tokens.get(pos);
    }

    private Token lookahead(int offset) {
        int index = pos + offset;
        if (index >= tokens.size()) {
            return tokens.get(tokens.size() - 1);
        }
        return tokens.get(index);
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException(message + " at token index " + pos);
    }
}
