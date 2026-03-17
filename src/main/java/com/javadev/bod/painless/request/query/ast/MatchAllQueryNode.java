package com.javadev.bod.painless.request.query.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MatchAllQueryNode implements QueryNode{
    @Override
    public String toString() {
        return "MatchAllQueryNode{}";
    }
}
