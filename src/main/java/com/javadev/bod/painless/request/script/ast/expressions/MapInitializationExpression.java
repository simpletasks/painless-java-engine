package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapInitializationExpression implements Expression {
    private final List<MapEntryExpression> entries;

    public MapInitializationExpression(List<MapEntryExpression> entries) {
        this.entries = entries;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Map<Object, Object> result = new LinkedHashMap<>();
        for (MapEntryExpression entry : entries) {
            Object key = entry.getKey().evaluate(ctx);
            Object value = entry.getValue().evaluate(ctx);
            result.put(key, value);
        }
        return result;
    }
}
