package com.javadev.bod.painless.request.script.ast.expressions;


import com.javadev.bod.painless.request.ExecutionContext;

public class StringExpression implements Expression {

    private final String value;

    public StringExpression(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return value;
    }
}
