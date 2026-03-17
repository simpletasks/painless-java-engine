package com.javadev.bod.painless.request.script.ast.expressions;

import lombok.Getter;

@Getter
public class MapEntryExpression {
    private final Expression key;
    private final Expression value;

    public MapEntryExpression(Expression key, Expression value) {
        this.key = key;
        this.value = value;
    }
}
