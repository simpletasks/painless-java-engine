package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;

public class RangeQueryPredicate implements QueryPredicate {

    private final PathRef field;

    private final Comparable<Object> gt;
    private final Comparable<Object> gte;
    private final Comparable<Object> lt;
    private final Comparable<Object> lte;

    @SuppressWarnings("unchecked")
    public RangeQueryPredicate(PathRef field,
                               Comparable<?> gt,
                               Comparable<?> gte,
                               Comparable<?> lt,
                               Comparable<?> lte) {

        this.field = field;
        this.gt = (Comparable<Object>) gt;
        this.gte = (Comparable<Object>) gte;
        this.lt = (Comparable<Object>) lt;
        this.lte = (Comparable<Object>) lte;
    }

    @Override
    public boolean matches(QueryEvaluationContext ctx) {

        Object value = ctx.getPathValue(field);

        if (!(value instanceof Comparable<?> comparable)) {
            return false;
        }

        Comparable<Object> actual = (Comparable<Object>) comparable;

        if (gt != null && actual.compareTo(gt) <= 0) {
            return false;
        }

        if (gte != null && actual.compareTo(gte) < 0) {
            return false;
        }

        if (lt != null && actual.compareTo(lt) >= 0) {
            return false;
        }

        if (lte != null && actual.compareTo(lte) > 0) {
            return false;
        }

        return true;
    }
}