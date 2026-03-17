package com.javadev.bod.painless.request.query.ast;

import com.javadev.bod.painless.request.PathRef;
import lombok.Getter;

@Getter
public class RangeQueryNode implements QueryNode {

    private final PathRef field;

    private final Comparable<?> gt;
    private final Comparable<?> gte;
    private final Comparable<?> lt;
    private final Comparable<?> lte;

    public RangeQueryNode(PathRef field,
                          Comparable<?> gt,
                          Comparable<?> gte,
                          Comparable<?> lt,
                          Comparable<?> lte) {

        this.field = field;
        this.gt = gt;
        this.gte = gte;
        this.lt = lt;
        this.lte = lte;
    }

    @Override
    public String toString() {
        return "RangeQueryNode{" +
                "field=" + field +
                ", gt=" + gt +
                ", gte=" + gte +
                ", lt=" + lt +
                ", lte=" + lte +
                '}';
    }
}