package com.javadev.bod.painless.request.script.ast;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumerates all supported binary operators in the interpreter.
 *
 * <p>Binary operators operate on two operands: a left operand and a right operand.
 * They represent arithmetic operations, comparisons, equality checks,
 * and boolean logic.</p>
 *
 * <p>Each operator is associated with a textual symbol as it appears
 * in the source script (for example {@code +}, {@code ==}, {@code &&}).</p>
 *
 * <p>The parser typically converts tokens into these enum values,
 * while the runtime uses the enum to determine evaluation semantics.</p>
 */
public enum BinaryOperator {

    /**
     * Logical OR operator ({@code ||}).
     */
    OR("||", 1),

    /**
     * Logical AND operator ({@code &&}).
     */
    AND("&&", 2),

    /**
     * Equality operator ({@code ==}).
     */
    EQ("==", 3),

    /**
     * Inequality operator ({@code !=}).
     */
    NE("!=", 3),

    /**
     * Greater-than operator ({@code >}).
     */
    GT(">", 4),

    /**
     * Greater-than-or-equal operator ({@code >=}).
     */
    GTE(">=", 4),

    /**
     * Less-than operator ({@code <}).
     */
    LT("<", 4),

    /**
     * Less-than-or-equal operator ({@code <=}).
     */
    LTE("<=", 4),

    /**
     * Addition operator ({@code +}).
     */
    ADD("+", 5),

    /**
     * Subtraction operator ({@code -}).
     */
    SUB("-", 5),

    /**
     * Multiplication operator ({@code *}).
     */
    MUL("*", 6),

    /**
     * Division operator ({@code /}).
     */
    DIV("/", 6),

    /**
     * Modulo operator ({@code %}).
     */
    MOD("%", 6);

    /**
     * Symbol used in the source script to represent this operator.
     */
    private final String symbol;

    /**
     * Operator precedence used by the parser.
     * Higher number = higher precedence.
     */
    private final int precedence;

    private static final Map<String, BinaryOperator> LOOKUP = new HashMap<>();

    static {
        for (BinaryOperator op : values()) {
            LOOKUP.put(op.symbol, op);
        }
    }

    BinaryOperator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    /**
     * Returns the textual symbol representing this operator.
     *
     * @return operator symbol as used in the source script
     */
    public String getSymbol() {
        return symbol;
    }

    public int getPrecedence() {
        return precedence;
    }

    /**
     * Resolves a binary operator from its textual symbol.
     *
     * @param symbol operator symbol
     * @return corresponding BinaryOperator
     * @throws IllegalArgumentException if symbol is not recognized
     */
    public static BinaryOperator fromSymbol(String symbol) {
        BinaryOperator op = LOOKUP.get(symbol);
        if (op == null) {
            throw new IllegalArgumentException("Unknown binary operator symbol: " + symbol);
        }
        return op;
    }

    public boolean hasHigherPrecedenceThan(BinaryOperator other) {
        return this.precedence > other.precedence;
    }

    public boolean isLogical() {
        return this == AND || this == OR;
    }

    public boolean isComparison() {
        return this == GT || this == GTE || this == LT || this == LTE || this == EQ || this == NE;
    }

    public boolean isArithmetic() {
        return this == ADD || this == SUB || this == MUL || this == DIV || this == MOD;
    }

}