package com.javadev.bod.painless.request.query.ast;

import com.javadev.bod.painless.request.PathRef;
import lombok.Getter;

import java.util.List;

@Getter
public class TermsQueryNode implements QueryNode {

    private final PathRef field;
    private final List<Object> values;

    public TermsQueryNode(PathRef field, List<Object> values) {
        this.field = field;
        this.values = values == null
                ? List.of()
                : List.copyOf(values);
    }

    @Override
    public String toString() {
        return "TermsQueryNode{" +
                "field=" + field +
                ", values=" + values +
                '}';
    }
}