package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExistsQueryPredicate implements  QueryPredicate {

    private final PathRef field;

    @Override
    public boolean matches(QueryEvaluationContext ctx) {
        if (!ctx.pathExists(field)) {
            return false;
        }
        return ctx.getPathValue(field) != null;
    }
}
