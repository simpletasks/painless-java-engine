package com.javadev.bod.painless.request.query.ast;

import com.javadev.bod.painless.request.PathRef;
import lombok.Getter;

@Getter
public class PrefixQueryNode implements QueryNode {

    private final PathRef field;
    private final String prefix;

    public PrefixQueryNode(PathRef field, String prefix) {
        this.field = field;
        this.prefix = prefix;
    }

    @Override
    public String toString() {
        return "PrefixQueryNode{" +
                "field=" + field +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}