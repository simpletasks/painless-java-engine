package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.query.ast.*;

import java.util.ArrayList;
import java.util.List;

public class QueryAstToRuntimeCompiler {

    public QueryPredicate compile(QueryNode node) {
        if (node instanceof MatchAllQueryNode) {
            return new MatchAllPredicate();
        }

        if (node instanceof TermQueryNode termNode) {
            return compileTerm(termNode);
        }

        if (node instanceof MatchQueryNode matchNode) {
            return compileMatch(matchNode);
        }

        if (node instanceof ExistsQueryNode existsNode) {
            return compileExists(existsNode);
        }

        if (node instanceof BoolQueryNode boolNode) {
            return compileBool(boolNode);
        }

        if (node instanceof RangeQueryNode rangeNode) {
            return compileRange(rangeNode);
        }

        if (node instanceof TermsQueryNode termsNode) {
            return compileTerms(termsNode);
        }

        if (node instanceof PrefixQueryNode prefixNode) {
            return compilePrefix(prefixNode);
        }

        throw new IllegalArgumentException("Unsupported query node: " + node.getClass().getName());
    }

    private QueryPredicate compileTerm(TermQueryNode node) {
        return new TermQueryPredicate(node.getField(), node.getValue());
    }

    private QueryPredicate compileExists(ExistsQueryNode node) {
        return new ExistsQueryPredicate(node.getField());
    }

    private QueryPredicate compileBool(BoolQueryNode node) {
        List<QueryPredicate> must = new ArrayList<>();
        for (QueryNode child : node.getMust()) {
            must.add(compile(child));
        }

        List<QueryPredicate> mustNot = new ArrayList<>();
        for (QueryNode child : node.getMustNot()) {
            mustNot.add(compile(child));
        }

        List<QueryPredicate> should = new ArrayList<>();
        for (QueryNode child : node.getShould()) {
            should.add(compile(child));
        }

        return new BoolQueryPredicate(must, mustNot, should);
    }

    private QueryPredicate compileRange(RangeQueryNode node) {
        return new RangeQueryPredicate(
                node.getField(),
                node.getGt(),
                node.getGte(),
                node.getLt(),
                node.getLte()
        );
    }

    private QueryPredicate compileTerms(TermsQueryNode node) {
        return new TermsQueryPredicate(
                node.getField(),
                node.getValues()
        );
    }

    private QueryPredicate compilePrefix(PrefixQueryNode node) {
        return new PrefixQueryPredicate(
                node.getField(),
                node.getPrefix()
        );
    }

    private QueryPredicate compileMatch(MatchQueryNode node) {
        return new MatchQueryPredicate(node.getField(), node.getValue());
    }
}
