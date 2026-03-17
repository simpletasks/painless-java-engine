package com.javadev.bod.painless.request.script.ast.nodes;

/**
 * Enumerates all supported unary operators in the interpreter.
 *
 * <p>Unary operators operate on a single operand.</p>
 */
public enum UnaryOperator {

    /**
     * Logical negation operator ({@code !}).
     */
    NOT("!"),

    /**
     * Unary plus operator ({@code +}).
     */
    PLUS("+"),

    /**
     * Unary minus operator ({@code -}).
     */
    MINUS("-");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
