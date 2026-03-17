package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class TermQueryPredicate implements  QueryPredicate {

    private final PathRef field;
    private final Object expectedValue;

    @Override
    public boolean matches(QueryEvaluationContext ctx) {
        Object actualValue = ctx.getPathValue(field);
        return Objects.equals(actualValue, expectedValue);
    }
}
