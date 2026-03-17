package com.javadev.bod.painless.request.query;

public class MatchAllPredicate implements QueryPredicate {

    @Override
    public boolean matches(QueryEvaluationContext ctx) {
        return true;
    }
}
