package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.LinkedHashMap;

public class NewLinkedHashMapExpression implements Expression {
    @Override
    public Object evaluate(ExecutionContext ctx) {
        return new LinkedHashMap<>();
    }
}
