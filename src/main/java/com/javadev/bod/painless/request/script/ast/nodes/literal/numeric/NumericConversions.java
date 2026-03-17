package com.javadev.bod.painless.request.script.ast.nodes.literal.numeric;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Helper conversions for promoted numeric runtime evaluation.
 */
public final class NumericConversions {

    private NumericConversions() {
    }

    public static BigDecimal toBigDecimal(Number value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }

        if (value instanceof BigInteger bi) {
            return new BigDecimal(bi);
        }

        if (value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long) {
            return BigDecimal.valueOf(value.longValue());
        }

        if (value instanceof Float || value instanceof Double) {
            return BigDecimal.valueOf(value.doubleValue());
        }

        throw new IllegalArgumentException(
                "Unsupported Number subtype for BigDecimal conversion: " + value.getClass().getName()
        );
    }

    public static BigInteger toBigInteger(Number value) {
        if (value instanceof BigInteger bi) {
            return bi;
        }

        if (value instanceof Byte
                || value instanceof Short
                || value instanceof Integer
                || value instanceof Long) {
            return BigInteger.valueOf(value.longValue());
        }

        throw new IllegalArgumentException(
                "Cannot convert non-integral Number to BigInteger safely: " + value.getClass().getName()
        );
    }
}
