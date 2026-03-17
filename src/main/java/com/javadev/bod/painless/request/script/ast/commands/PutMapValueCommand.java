package com.javadev.bod.painless.request.script.ast.commands;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;

import java.util.Map;

public class PutMapValueCommand implements Command {
    private final String variableName;
    private final String key;
    private final Expression valueExpr;

    public PutMapValueCommand(String variableName, String key, Expression valueExpr) {
        this.variableName = variableName;
        this.key = key;
        this.valueExpr = valueExpr;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute(ExecutionContext ctx) {
        Map<String, Object> map = (Map<String, Object>) ctx.getVariable(variableName);
        map.put(key, valueExpr.evaluate(ctx));
    }
}
