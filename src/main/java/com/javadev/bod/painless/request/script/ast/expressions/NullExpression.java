package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

public class NullExpression implements Expression {
    @Override
    public Object evaluate(ExecutionContext ctx) {
        return null;
    }
}
