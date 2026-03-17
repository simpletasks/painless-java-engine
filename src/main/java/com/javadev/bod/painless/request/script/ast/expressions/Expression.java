package com.javadev.bod.painless.request.script.ast.expressions;


import com.javadev.bod.painless.request.ExecutionContext;

public interface Expression {
    Object evaluate(ExecutionContext ctx);
}
