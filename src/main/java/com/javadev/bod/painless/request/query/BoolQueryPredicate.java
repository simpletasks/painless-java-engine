package com.javadev.bod.painless.request.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BoolQueryPredicate implements  QueryPredicate {

    private final List<QueryPredicate> must;
    private final List<QueryPredicate> mustNot;
    private final List<QueryPredicate> should;

    @Override
    public boolean matches(QueryEvaluationContext ctx) {
        for (QueryPredicate predicate : must) {
            if (!predicate.matches(ctx)) {
                return false;
            }
        }

        for (QueryPredicate predicate : mustNot) {
            if (predicate.matches(ctx)) {
                return false;
            }
        }

        if (!should.isEmpty()) {
            boolean anyShouldMatch = false;
            for (QueryPredicate predicate : should) {
                if (predicate.matches(ctx)) {
                    anyShouldMatch = true;
                    break;
                }
            }
            if (!anyShouldMatch) {
                return false;
            }
        }

        return true;
    }
}
