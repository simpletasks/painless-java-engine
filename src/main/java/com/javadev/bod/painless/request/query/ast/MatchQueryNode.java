package com.javadev.bod.painless.request.query.ast;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MatchQueryNode implements QueryNode {

    private final PathRef field;
    private final Object value;

    @Override
    public String toString() {
        return "MatchQueryNode{" +
                "field=" + field +
                ", value=" + value +
                '}';
    }
}