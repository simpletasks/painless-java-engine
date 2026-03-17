package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TermsQueryPredicate implements QueryPredicate {

    private final PathRef field;
    private final Set<Object> allowedValues;

    public TermsQueryPredicate(PathRef field, List<Object> values) {
        this.field = field;
        this.allowedValues = new HashSet<>(values);
    }

    @Override
    public boolean matches(QueryEvaluationContext ctx) {

        Object value = ctx.getPathValue(field);

        return allowedValues.contains(value);
    }
}