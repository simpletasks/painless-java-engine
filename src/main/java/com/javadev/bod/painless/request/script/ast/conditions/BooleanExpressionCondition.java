package com.javadev.bod.painless.request.script.ast.conditions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;

public class BooleanExpressionCondition implements Condition {

    private final Expression expression;

    public BooleanExpressionCondition(Expression expression) {
        this.expression = expression;
    }

    @Override
    public boolean test(ExecutionContext ctx) {
        Object value = expression.evaluate(ctx);

        if (!(value instanceof Boolean b)) {
            throw new IllegalArgumentException(
                    "If condition must evaluate to boolean, but got: " +
                            (value == null ? "null" : value.getClass().getName())
            );
        }

        return b;
    }
}
