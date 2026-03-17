package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;

public class PrefixQueryPredicate implements QueryPredicate {

    private final PathRef field;
    private final String prefix;

    public PrefixQueryPredicate(PathRef field, String prefix) {
        this.field = field;
        this.prefix = prefix;
    }

    @Override
    public boolean matches(QueryEvaluationContext ctx) {

        Object value = ctx.getPathValue(field);

        if (!(value instanceof String str)) {
            return false;
        }

        return str.startsWith(prefix);
    }
}