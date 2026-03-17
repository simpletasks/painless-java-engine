package com.javadev.bod.painless.request.script.ast.conditions;


import com.javadev.bod.painless.request.ExecutionContext;

public interface Condition {
    boolean test(ExecutionContext ctx);
}
