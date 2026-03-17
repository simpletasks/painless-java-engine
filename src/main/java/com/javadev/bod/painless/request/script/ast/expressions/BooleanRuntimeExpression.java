package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

public class BooleanRuntimeExpression implements Expression {

    private final boolean value;

    public BooleanRuntimeExpression(boolean value) {
        this.value = value;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return value;
    }
}