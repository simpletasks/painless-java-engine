package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;
import java.util.Objects;

@AllArgsConstructor
@Getter
public class MatchQueryPredicate implements QueryPredicate {

    private final PathRef field;
    private final Object expectedValue;

    @Override
    public boolean matches(QueryEvaluationContext ctx) {
        Object actualValue = ctx.getPathValue(field);

        if (actualValue == null || expectedValue == null) {
            return Objects.equals(actualValue, expectedValue);
        }

        if (actualValue instanceof String actualString && expectedValue instanceof String expectedString) {
            String actualNormalized = actualString.toLowerCase(Locale.ROOT);
            String expectedNormalized = expectedString.toLowerCase(Locale.ROOT);
            return actualNormalized.contains(expectedNormalized);
        }

        return Objects.equals(actualValue, expectedValue);
    }
}