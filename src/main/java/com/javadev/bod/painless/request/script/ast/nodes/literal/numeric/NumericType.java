package com.javadev.bod.painless.request.script.ast.nodes.literal.numeric;

/**
 * Defines numeric runtime categories used for type promotion during
 * arithmetic and comparison evaluation.
 *
 * <p>Higher priority types dominate lower priority types.</p>
 */
public enum NumericType {

    BIG_DECIMAL(5),
    DOUBLE(4),
    FLOAT(3),
    BIG_INTEGER(2),
    LONG(1);

    private final int priority;

    NumericType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
