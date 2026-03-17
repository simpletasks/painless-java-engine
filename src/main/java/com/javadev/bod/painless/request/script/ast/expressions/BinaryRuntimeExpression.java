package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.BinaryOperator;
import com.javadev.bod.painless.request.script.ast.nodes.literal.numeric.NumericConversions;
import com.javadev.bod.painless.request.script.ast.nodes.literal.numeric.NumericPromotion;
import com.javadev.bod.painless.request.script.ast.nodes.literal.numeric.NumericType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class BinaryRuntimeExpression implements Expression {

    private final Expression leftValue;
    private final Expression rightValue;
    private final BinaryOperator operator;

    public BinaryRuntimeExpression(Expression leftValue, Expression rightValue, BinaryOperator operator) {
        this.leftValue = leftValue;
        this.rightValue = rightValue;
        this.operator = operator;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {

        // short-circuit behavior &&
        if (operator == BinaryOperator.AND) {
            return andShortCircuit(ctx);
        }

        // short-circuit behavior ||
        if (operator == BinaryOperator.OR) {
            return orShortCircuit(ctx);
        }

        // all other operators - evaluate expressions
        Object leftValue = this.leftValue.evaluate(ctx);
        Object rightValue = this.rightValue.evaluate(ctx);

        // numeric null coercion
        if (leftValue == null && rightValue instanceof Number) {
            leftValue = 0;
        }

        if (rightValue == null && leftValue instanceof Number) {
            rightValue = 0;
        }

        if (operator == BinaryOperator.ADD) {

            // string concatenation
            if (leftValue instanceof String || rightValue instanceof String) {
                return String.valueOf(leftValue) + String.valueOf(rightValue);
            }

            if (leftValue == null && rightValue == null) {
                return 0;
            }
        }

        if (leftValue instanceof Number leftNumber && rightValue instanceof Number rightNumber) {
            return evaluateNumbers(leftNumber, rightNumber);
        }

        if (operator == BinaryOperator.EQ) {
            return Objects.equals(leftValue, rightValue);
        }

        if (operator == BinaryOperator.NE) {
            return !Objects.equals(leftValue, rightValue);
        }

        if (leftValue instanceof Boolean l && rightValue instanceof Boolean r) {
            return switch (operator) {
                case EQ -> l == r;
                case NE -> l != r;
                default -> throw unsupported(leftValue, rightValue);
            };
        }

        throw unsupported(leftValue, rightValue);
    }

    private Object orShortCircuit(ExecutionContext ctx) {
        Object leftValue = this.leftValue.evaluate(ctx);

        if (!(leftValue instanceof Boolean lb)) {
            throw new IllegalArgumentException("Left operand of " + operator.getSymbol() + " must be boolean");
        }

        if (lb) {
            return true;
        }

        Object rightValue = this.rightValue.evaluate(ctx);

        if (!(rightValue instanceof Boolean rb)) {
            throw new IllegalArgumentException("Right operand of " + operator.getSymbol() + " must be boolean");
        }

        return rb;
    }

    private Object andShortCircuit(ExecutionContext ctx) {
        Object leftValue = this.leftValue.evaluate(ctx);

        if (!(leftValue instanceof Boolean lb)) {
            throw new IllegalArgumentException("Left operand of " + operator.getSymbol() + " must be boolean");
        }

        if (!lb) {
            return false;
        }

        Object rightValue = this.rightValue.evaluate(ctx);

        if (!(rightValue instanceof Boolean rb)) {
            throw new IllegalArgumentException("Right operand of " + operator.getSymbol() + " must be boolean");
        }

        return rb;
    }

    private Object evaluateNumbers(Number leftNumber, Number rightNumber) {
        NumericType promotedType = NumericPromotion.promote(leftNumber, rightNumber);

        return switch (promotedType) {
            case BIG_DECIMAL -> evaluateBigDecimal(
                    NumericConversions.toBigDecimal(leftNumber),
                    NumericConversions.toBigDecimal(rightNumber)
            );
            case DOUBLE -> evaluateDouble(
                    leftNumber.doubleValue(),
                    rightNumber.doubleValue()
            );
            case FLOAT -> evaluateFloat(
                    leftNumber.floatValue(),
                    rightNumber.floatValue()
            );
            case BIG_INTEGER -> evaluateBigInteger(
                    NumericConversions.toBigInteger(leftNumber),
                    NumericConversions.toBigInteger(rightNumber)
            );
            case LONG -> evaluateLong(
                    leftNumber.longValue(),
                    rightNumber.longValue()
            );
        };
    }

    private Object evaluateBigDecimal(BigDecimal l, BigDecimal r) {
        return switch (operator) {
            case ADD -> l.add(r);
            case SUB -> l.subtract(r);
            case MUL -> l.multiply(r);
            case DIV -> l.divide(r, java.math.MathContext.DECIMAL128);
            case MOD -> l.remainder(r);
            case GT -> l.compareTo(r) > 0;
            case GTE -> l.compareTo(r) >= 0;
            case LT -> l.compareTo(r) < 0;
            case LTE -> l.compareTo(r) <= 0;
            case EQ -> l.compareTo(r) == 0;
            case NE -> l.compareTo(r) != 0;
            default -> throw unsupported(l, r);
        };
    }

    private Object evaluateBigInteger(BigInteger l, BigInteger r) {
        return switch (operator) {
            case ADD -> l.add(r);
            case SUB -> l.subtract(r);
            case MUL -> l.multiply(r);
            case DIV -> l.divide(r);
            case MOD -> l.remainder(r);
            case GT -> l.compareTo(r) > 0;
            case GTE -> l.compareTo(r) >= 0;
            case LT -> l.compareTo(r) < 0;
            case LTE -> l.compareTo(r) <= 0;
            case EQ -> l.compareTo(r) == 0;
            case NE -> l.compareTo(r) != 0;
            default -> throw unsupported(l, r);
        };
    }

    private Object evaluateDouble(double l, double r) {
        return switch (operator) {
            case ADD -> l + r;
            case SUB -> l - r;
            case MUL -> l * r;
            case DIV -> l / r;
            case MOD -> l % r;
            case GT -> l > r;
            case GTE -> l >= r;
            case LT -> l < r;
            case LTE -> l <= r;
            case EQ -> l == r;
            case NE -> l != r;
            default -> throw unsupported(l, r);
        };
    }

    private Object evaluateFloat(float l, float r) {
        return switch (operator) {
            case ADD -> l + r;
            case SUB -> l - r;
            case MUL -> l * r;
            case DIV -> l / r;
            case MOD -> l % r;
            case GT -> l > r;
            case GTE -> l >= r;
            case LT -> l < r;
            case LTE -> l <= r;
            case EQ -> l == r;
            case NE -> l != r;
            default -> throw unsupported(l, r);
        };
    }

    private Object evaluateLong(long l, long r) {
        return switch (operator) {
            case ADD -> l + r;
            case SUB -> l - r;
            case MUL -> l * r;
            case DIV -> l / r;
            case MOD -> l % r;
            case GT -> l > r;
            case GTE -> l >= r;
            case LT -> l < r;
            case LTE -> l <= r;
            case EQ -> l == r;
            case NE -> l != r;
            default -> throw unsupported(l, r);
        };
    }

    private IllegalArgumentException unsupported(Object leftValue, Object rightValue) {
        return new IllegalArgumentException(
                "Unsupported operands for operator " + operator.getSymbol() +
                        ": left=" + typeOf(leftValue) +
                        ", right=" + typeOf(rightValue)
        );
    }

    private String typeOf(Object value) {
        return value == null ? "null" : value.getClass().getName();
    }
}
