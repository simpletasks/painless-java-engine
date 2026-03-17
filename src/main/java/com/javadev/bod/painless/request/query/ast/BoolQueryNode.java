package com.javadev.bod.painless.request.query.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class BoolQueryNode implements QueryNode {

    private final List<QueryNode> must;
    private final List<QueryNode> mustNot;
    private final List<QueryNode> should;

    @Override
    public String toString() {
        return "BoolQueryNode{" +
                "must=" + must +
                ", mustNot=" + mustNot +
                ", should=" + should +
                '}';
    }
}
