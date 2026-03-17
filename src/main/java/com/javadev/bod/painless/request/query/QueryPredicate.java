package com.javadev.bod.painless.request.query;

public interface QueryPredicate {
    boolean matches(QueryEvaluationContext ctx);

}
