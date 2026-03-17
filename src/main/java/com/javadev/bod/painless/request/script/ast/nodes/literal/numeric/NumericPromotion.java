package com.javadev.bod.painless.request.script.ast.nodes.literal.numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Utility class for resolving runtime numeric type promotion rules.
 *
 * <p>Promotion order:</p>
 * <pre>
 * BigDecimal
 * Double
 * Float
 * BigInteger
 * Long
 * </pre>
 */
public final class NumericPromotion {

    private NumericPromotion() {
    }

    /**
     * Resolves the promoted numeric type for two numeric operands.
     *
     * @param left left operand
     * @param right right operand
     * @return promoted numeric type
     */
    public static NumericType promote(Number left, Number right) {
        NumericType leftType = classify(left);
        NumericType rightType = classify(right);

        return leftType.getPriority() >= rightType.getPriority()
                ? leftType
                : rightType;
    }

    /**
     * Classifies a runtime {@link Number} into one of the supported numeric categories.
     *
     * @param value numeric value
     * @return numeric runtime category
     */
    public static NumericType classify(Number value) {
        if (value instanceof BigDecimal) {
            return NumericType.BIG_DECIMAL;
        }

        if (value instanceof Double) {
            return NumericType.DOUBLE;
        }

        if (value instanceof Float) {
            return NumericType.FLOAT;
        }

        if (value instanceof BigInteger) {
            return NumericType.BIG_INTEGER;
        }

        if (value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long) {
            return NumericType.LONG;
        }

        throw new IllegalArgumentException(
                "Unsupported Number subtype: " + value.getClass().getName()
        );
    }
}
