package com.javadev.bod.painless.request.script.ast.tokenizer;

/**
 * Enumeration of all token types produced by the tokenizer.
 *
 * <p>Each token may optionally have a textual symbol representing
 * the exact character sequence from the source code.</p>
 */
public enum TokenType {

    IDENTIFIER(null),
    STRING(null),
    INTEGER(null),
    DECIMAL(null),

    IF("if"),
    ELSE("else"),
    NULL("null"),
    TRUE("true"),
    FALSE("false"),
    RETURN("return"),
    INSTANCEOF("instanceof"),

    ASSIGN("="),
    EQEQ("=="),
    NE("!="),
    BANG("!"),

    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),

    OR_OR("||"),
    AND_AND("&&"),

    PLUS("+"),
    MINUS("-"),
    STAR("*"),
    SLASH("/"),
    PERCENT("%"),

    PLUS_PLUS("++"),
    MINUS_MINUS("--"),

    PLUS_EQ("+="),
    MINUS_EQ("-="),
    STAR_EQ("*="),
    SLASH_EQ("/="),
    PERCENT_EQ("%="),

    LBRACKET("["),
    RBRACKET("]"),
    LBRACE("{"),
    RBRACE("}"),
    LPAREN("("),
    RPAREN(")"),

    DOT("."),
    COMMA(","),
    SEMICOLON(";"),
    COLON(":"),

    COLON_RBRACKET(":]"),

    EOF(null);

    private final String symbol;

    TokenType(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Returns the textual representation of the token
     * as it appears in the source code.
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns true if the token represents an operator.
     */
    public boolean isOperator() {
        return switch (this) {
            case ASSIGN,
                 EQEQ, NE, BANG,
                 GT, GTE, LT, LTE,
                 OR_OR, AND_AND,
                 PLUS, MINUS, STAR, SLASH, PERCENT,
                 PLUS_EQ, MINUS_EQ, STAR_EQ, SLASH_EQ, PERCENT_EQ,
                 PLUS_PLUS, MINUS_MINUS -> true;
            default -> false;
        };
    }

    /**
     * Returns true if the token represents an assignment operator.
     */
    public boolean isAssignment() {
        return switch (this) {
            case ASSIGN,
                 PLUS_EQ, MINUS_EQ, STAR_EQ, SLASH_EQ, PERCENT_EQ -> true;
            default -> false;
        };
    }

    /**
     * Returns true if the token represents a literal value.
     */
    public boolean isLiteral() {
        return switch (this) {
            case STRING,
                 NULL,
                 INTEGER,
                 DECIMAL -> true;
            default -> false;
        };
    }
}
