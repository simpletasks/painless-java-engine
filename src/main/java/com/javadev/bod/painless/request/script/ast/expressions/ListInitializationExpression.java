package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.ArrayList;
import java.util.List;

public class ListInitializationExpression implements Expression {
    private final List<Expression> elements;

    public ListInitializationExpression(List<Expression> elements) {
        this.elements = elements;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        List<Object> result = new ArrayList<>();
        for (Expression element : elements) {
            result.add(element.evaluate(ctx));
        }
        return result;
    }
}
