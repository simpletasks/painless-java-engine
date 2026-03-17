package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.List;
import java.util.Map;

public class CastExpression implements Expression {
    private final String targetType;
    private final Expression expression;

    public CastExpression(String targetType, Expression expression) {
        this.targetType = targetType;
        this.expression = expression;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object value = expression.evaluate(ctx);

        if (value == null) {
            return null;
        }

        return switch (targetType) {
            case "String" -> String.class.cast(value);
            case "Map" -> Map.class.cast(value);
            case "List" -> List.class.cast(value);
            case "Object", "def" -> value;
            default -> throw new IllegalArgumentException("Unsupported cast target type: " + targetType);
        };
    }
}
