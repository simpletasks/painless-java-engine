package com.javadev.bod.painless.request.script.ast.conditions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;

import java.util.Objects;

public class EqualsCondition implements Condition {

    private final Expression left;
    private final Expression right;

    public EqualsCondition(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean test(ExecutionContext ctx) {
        return Objects.equals(left.evaluate(ctx), right.evaluate(ctx));
    }
}
