package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.List;
import java.util.Map;

public class InstanceOfExpression implements Expression {
    private final Expression expression;
    private final String typeName;

    public InstanceOfExpression(Expression expression, String typeName) {
        this.expression = expression;
        this.typeName = typeName;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object value = expression.evaluate(ctx);

        return switch (typeName) {
            case "String" -> value instanceof String;
            case "Map" -> value instanceof Map;
            case "List" -> value instanceof List;
            case "Integer", "int" -> value instanceof Integer;
            case "Long", "long" -> value instanceof Long;
            case "Boolean", "boolean" -> value instanceof Boolean;
            case "Object", "def" -> true;
            default -> throw new IllegalArgumentException("Unsupported instanceof type: " + typeName);
        };
    }
}
