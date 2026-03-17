package com.javadev.bod.painless;

import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.script.ast.commands.*;
import com.javadev.bod.painless.request.script.ast.expressions.*;
import com.javadev.bod.painless.request.script.ast.conditions.Condition;
import com.javadev.bod.painless.request.script.ast.conditions.EqualsCondition;
import com.javadev.bod.painless.request.script.ast.conditions.NotEqualsCondition;
import com.javadev.bod.painless.request.script.ast.tokenizer.Token;
import com.javadev.bod.painless.request.script.ast.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;

@Deprecated(forRemoval = true)
public class PainlessSubsetParser {
    final List<Token> tokens;
    private int pos = 0;

    public PainlessSubsetParser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Command parseScript() {
        List<Command> commands = new ArrayList<>();

        while (!is(TokenType.EOF)) {
            if (is(TokenType.SEMICOLON)) {
                consume();
                continue;
            }
            commands.add(parseStatement());
        }

        return new BlockCommand(commands);
    }

    private Command parseStatement() {
        if (match(TokenType.IF)) {
            return parseIf();
        }

        if (match(TokenType.RETURN)) {
            optionalSemicolon();
            return new ReturnCommand();
        }

        if (isTypedVariableDeclaration()) {
            return parseTypedVariableDeclaration();
        }

        return parseAssignment();
    }

    private boolean isTypedVariableDeclaration() {
        return is(TokenType.IDENTIFIER)
                && lookahead(1).getType() == TokenType.IDENTIFIER
                && lookahead(2).getType() == TokenType.ASSIGN;
    }

    private Command parseTypedVariableDeclaration() {
        expect(TokenType.IDENTIFIER); // npr. Map
        Token variable = expect(TokenType.IDENTIFIER); // npr. obj
        expect(TokenType.ASSIGN);

        Expression expr = parseExpression();
        optionalSemicolon();

        return new AssignVariableCommand(variable.getText(), expr);
    }

    private Command parseIf() {
        expect(TokenType.LPAREN);
        Condition condition = parseCondition();
        expect(TokenType.RPAREN);

        List<Command> thenCommands = parseStatementOrBlock();
        List<Command> elseCommands = List.of();

        if (match(TokenType.ELSE)) {
            elseCommands = parseStatementOrBlock();
        }

        return new IfCommand(condition, thenCommands, elseCommands);
    }

    private List<Command> parseBlockStatements() {
        expect(TokenType.LBRACE);

        List<Command> commands = new ArrayList<>();
        while (!is(TokenType.RBRACE)) {
            if (is(TokenType.SEMICOLON)) {
                consume();
                continue;
            }
            commands.add(parseStatement());
        }

        expect(TokenType.RBRACE);
        return commands;
    }

    private List<Command> parseStatementOrBlock() {
        if (is(TokenType.LBRACE)) {
            return parseBlockStatements();
        }

        return List.of(parseStatement());
    }

    private Command parseAssignment() {
        AssignmentTarget target = parseAssignmentTarget();
        expect(TokenType.ASSIGN);

        if (target.type == AssignmentTargetType.VARIABLE && isEmptyMapLiteral()) {
            parseEmptyMapLiteral();
            optionalSemicolon();
            return new CreateMapCommand(target.variableName);
        }

        Expression expression = parseExpression();
        optionalSemicolon();

        return new AssignPathCommand(target.path, expression);
    }

    private AssignmentTarget parseAssignmentTarget() {
        Token first = expect(TokenType.IDENTIFIER);
        String root = first.getText();

        List<String> segments = new ArrayList<>();
        segments.add(root);

        while (match(TokenType.DOT)) {
            segments.add(expect(TokenType.IDENTIFIER).getText());
        }

        while (match(TokenType.LBRACKET)) {
            String key = expect(TokenType.STRING).getText();
            expect(TokenType.RBRACKET);
            segments.add(key);
        }

        if (segments.size() == 1) {
            return new AssignmentTarget(
                    AssignmentTargetType.VARIABLE,
                    root,
                    new PathRef(segments)
            );
        }

        return new AssignmentTarget(
                AssignmentTargetType.VARIABLE_OR_PATH,
                root,
                new PathRef(segments)
        );
    }

    private Condition parseCondition() {
        Expression left = parseExpression();

        if (match(TokenType.NE)) {
            Expression right = parseExpression();
            return new NotEqualsCondition(left, right);
        }

        if (match(TokenType.EQEQ)) {
            Expression right = parseExpression();
            return new EqualsCondition(left, right);
        }

        throw error("Expected == or !=");
    }

    private Expression parseExpression() {
        Expression expr = parsePrimaryExpression();

        while (match(TokenType.LBRACKET)) {
            Expression key = parseExpression();
            expect(TokenType.RBRACKET);
            expr = new IndexAccessExpression(expr, key);
        }

        return expr;
    }

    private Expression parsePrimaryExpression() {
        if (match(TokenType.NULL)) {
            return new NullExpression();
        }

        if (is(TokenType.STRING)) {
            return new StringExpression(consume().getText());
        }

        if (is(TokenType.IDENTIFIER)) {
            Token id = consume();

            if (match(TokenType.LPAREN)) {
                List<Expression> args = new ArrayList<>();

                if (!is(TokenType.RPAREN)) {
                    args.add(parseExpression());
                    while (match(TokenType.COMMA)) {
                        args.add(parseExpression());
                    }
                }

                expect(TokenType.RPAREN);
                return new FunctionCallExpression(id.getText(), args);
            }

            List<String> segments = new ArrayList<>();
            segments.add(id.getText());

            while (match(TokenType.DOT)) {
                segments.add(expect(TokenType.IDENTIFIER).getText());
            }

            if (segments.size() == 1 && !"ctx".equals(segments.get(0))) {
                return new VariableExpression(segments.get(0));
            }

            return new PathExpression(new PathRef(segments));
        }

        throw error("Unsupported expression");
    }

    private boolean isEmptyMapLiteral() {
        return is(TokenType.LBRACKET) && lookahead(1).getType() == TokenType.COLON_RBRACKET;
    }

    private void parseEmptyMapLiteral() {
        expect(TokenType.LBRACKET);
        expect(TokenType.COLON_RBRACKET);
    }

    private void optionalSemicolon() {
        if (is(TokenType.SEMICOLON)) {
            consume();
        }
    }

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

    private enum AssignmentTargetType {
        VARIABLE,
        VARIABLE_OR_PATH
    }

    private static final class AssignmentTarget {
        private final AssignmentTargetType type;
        private final String variableName;
        private final PathRef path;

        private AssignmentTarget(AssignmentTargetType type, String variableName, PathRef path) {
            this.type = type;
            this.variableName = variableName;
            this.path = path;
        }
    }
}
