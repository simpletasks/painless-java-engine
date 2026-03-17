package com.javadev.bod.painless.request;

import com.javadev.bod.painless.request.query.ast.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class QueryMapParser {

    @SuppressWarnings("unchecked")
    public static QueryNode parse(Map<String, Object> query) {

        if (query.containsKey("term")) {

            Map<String, Object> term = (Map<String, Object>) query.get("term");
            String field = term.keySet().iterator().next();
            Object value = term.get(field);

            return new TermQueryNode(toPathRef(field), value);
        }

        if (query.containsKey("match")) {

            Map<String, Object> match = (Map<String, Object>) query.get("match");
            String field = match.keySet().iterator().next();
            Object value = match.get(field);

            return new MatchQueryNode(toPathRef(field), value);
        }

        if (query.containsKey("terms")) {

            Map<String, Object> terms = (Map<String, Object>) query.get("terms");
            String field = terms.keySet().iterator().next();
            List<Object> values = (List<Object>) terms.get(field);

            return new TermsQueryNode(toPathRef(field), values);
        }

        if (query.containsKey("prefix")) {

            Map<String, Object> prefix = (Map<String, Object>) query.get("prefix");
            String field = prefix.keySet().iterator().next();
            String value = (String) prefix.get(field);

            return new PrefixQueryNode(toPathRef(field), value);
        }

        if (query.containsKey("range")) {

            Map<String, Object> range = (Map<String, Object>) query.get("range");
            String field = range.keySet().iterator().next();
            Map<String, Object> params = (Map<String, Object>) range.get(field);

            return new RangeQueryNode(
                    toPathRef(field),
                    (Comparable<?>) params.get("gt"),
                    (Comparable<?>) params.get("gte"),
                    (Comparable<?>) params.get("lt"),
                    (Comparable<?>) params.get("lte")
            );
        }

        if (query.containsKey("exists")) {

            Map<String, Object> exists = (Map<String, Object>) query.get("exists");
            String field = (String) exists.get("field");

            return new ExistsQueryNode(toPathRef(field));
        }

        if (query.containsKey("bool")) {

            Map<String, Object> bool = (Map<String, Object>) query.get("bool");

            List<QueryNode> must = parseList(bool.get("must"));
            List<QueryNode> mustNot = parseList(bool.get("must_not"));
            List<QueryNode> should = parseList(bool.get("should"));

            return new BoolQueryNode(must, mustNot, should);
        }

        throw new IllegalArgumentException("Unsupported query: " + query);
    }

    private static List<QueryNode> parseList(Object obj) {

        if (obj == null) {
            return List.of();
        }

        List<Map<String, Object>> list = (List<Map<String, Object>>) obj;

        List<QueryNode> nodes = new ArrayList<>();

        for (Map<String, Object> map : list) {
            nodes.add(parse(map));
        }

        return nodes;
    }

    private static PathRef toPathRef(String field) {
        return new PathRef(Arrays.asList(field.split("\\.")));
    }
}