package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.Map;

public class IndexAccessExpression implements Expression {
    private final Expression target;
    private final Expression key;

    public IndexAccessExpression(Expression target, Expression key) {
        this.target = target;
        this.key = key;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object evaluate(ExecutionContext ctx) {
        Object targetValue = target.evaluate(ctx);
        Object keyValue = key.evaluate(ctx);

        if (targetValue instanceof Map<?, ?> map) {
            return ((Map<String, Object>) map).get(String.valueOf(keyValue));
        }

        return null;
    }
}
