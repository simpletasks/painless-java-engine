package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.nodes.UnaryOperator;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Runtime expression for unary operators.
 */
public class UnaryRuntimeExpression implements Expression {

    private final UnaryOperator operator;
    private final Expression operand;

    public UnaryRuntimeExpression(UnaryOperator operator, Expression operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object value = operand.evaluate(ctx);

        return switch (operator) {
            case NOT -> evaluateNot(value);
            case PLUS -> evaluatePlus(value);
            case MINUS -> evaluateMinus(value);
        };
    }

    private Object evaluateNot(Object value) {
        if (value instanceof Boolean b) {
            return !b;
        }

        throw new IllegalArgumentException(
                "Unary operator " + operator.getSymbol() +
                        " requires boolean operand, but got: " + typeOf(value)
        );
    }

    private Object evaluatePlus(Object value) {
        if (value instanceof Number n) {
            return normalizeNumeric(n);
        }

        throw new IllegalArgumentException(
                "Unary operator " + operator.getSymbol() +
                        " requires numeric operand, but got: " + typeOf(value)
        );
    }

    private Object evaluateMinus(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd.negate();
        }

        if (value instanceof BigInteger bi) {
            return bi.negate();
        }

        if (value instanceof Double d) {
            return -d;
        }

        if (value instanceof Float f) {
            return -f;
        }

        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return -((Number) value).longValue();
        }

        throw new IllegalArgumentException(
                "Unary operator " + operator.getSymbol() +
                        " requires numeric operand, but got: " + typeOf(value)
        );
    }

    /**
     * Unary plus keeps the numeric value, but normalizes integral subtypes to long.
     */
    private Object normalizeNumeric(Number n) {
        if (n instanceof BigDecimal || n instanceof BigInteger || n instanceof Double || n instanceof Float) {
            return n;
        }

        return n.longValue();
    }

    private String typeOf(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }

    @Override
    public String toString() {
        return "UnaryRuntimeExpression{" +
                "operator=" + operator +
                ", operand=" + operand +
                '}';
    }
}
