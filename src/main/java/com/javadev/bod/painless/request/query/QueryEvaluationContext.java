package com.javadev.bod.painless.request.query;

import com.javadev.bod.painless.request.PathRef;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class QueryEvaluationContext {
    private final Map<String, Object> source;

    public Object getPathValue(PathRef path) {
        Object current = source;

        for (String segment : path.segments()) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = map.get(segment);
        }

        return current;
    }

    public boolean pathExists(PathRef path) {
        Object current = source;

        for (String segment : path.segments()) {
            if (!(current instanceof Map<?, ?> map)) {
                return false;
            }
            if (!map.containsKey(segment)) {
                return false;
            }
            current = map.get(segment);
        }

        return true;
    }
}
