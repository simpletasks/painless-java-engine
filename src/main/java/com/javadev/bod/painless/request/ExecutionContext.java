package com.javadev.bod.painless.request;

import com.javadev.bod.painless.request.script.ScriptFunctions;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Runtime context used for script evaluation.
 *
 * <p>It contains:
 * <ul>
 *   <li>the input document being processed by the script</li>
 *   <li>the script's local variables</li>
 *   <li>the registered script functions</li>
 * </ul>
 *
 * <h3>Semantic root objects</h3>
 * <ul>
 *   <li>{@code ctx._source} -> points to {@code document}</li>
 *   <li>a variable name -> points to a value stored in {@code variables}</li>
 * </ul>
 *
 * <h3>Example model</h3>
 *
 * <pre>
 * ExecutionContext
 * ├── document
 * │   ├── status = "READY"
 * │   └── meta
 * │       └── timestamp = "2024-01-15T10:15:30Z"
 * ├── variables
 * │   └── count = 10
 * └── functions
 * </pre>
 *
 * <p>Then the following lookups are valid:
 *
 * <pre>
 * ctx._source.status         -> "READY"
 * ctx._source.meta.timestamp -> "2024-01-15T10:15:30Z"
 * count                      -> 10
 * </pre>
 *
 * <p>The current implementation supports:
 * <ul>
 *   <li>reading and writing through {@code ctx._source} paths</li>
 *   <li>reading and writing through paths starting from an existing variable</li>
 *   <li>automatic creation of missing intermediate map nodes as {@link LinkedHashMap}
 *       during path assignment</li>
 * </ul>
 */
public class ExecutionContext {
    private final Map<String, Object> document;
    private final Map<String, Object> variables = new HashMap<>();
    private final ScriptFunctions functions;

    public ExecutionContext(Map<String, Object> document, ScriptFunctions functions) {
        this.document = document;
        this.functions = functions;
    }

    public Map<String, Object> document() {
        return document;
    }

    public ScriptFunctions functions() {
        return functions;
    }

    public Object getVariable(String name) {
        return variables.get(name);
    }

    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    public boolean hasVariable(String name) {
        return variables.containsKey(name);
    }

    @SuppressWarnings("unchecked")
    public Object getPathValue(PathRef path) {
        if (path.segments().isEmpty()) {
            return null;
        }

        List<String> segments = path.segments();

        Object current;
        int startIndex;

        if ("ctx".equals(segments.get(0))) {
            if (segments.size() >= 2 && "_source".equals(segments.get(1))) {
                current = document;
                startIndex = 2;
            } else {
                return null;
            }
        } else if (hasVariable(segments.get(0))) {
            current = getVariable(segments.get(0));
            startIndex = 1;
        } else {
            return null;
        }

        for (int i = startIndex; i < segments.size(); i++) {
            if (!(current instanceof Map<?, ?> map)) {
                return null;
            }
            current = ((Map<String, Object>) map).get(segments.get(i));
        }

        return current;
    }

    @SuppressWarnings("unchecked")
    public void setPathValue(PathRef path, Object value) {
        if (path.segments().isEmpty()) {
            throw new IllegalArgumentException("Empty path");
        }

        List<String> segments = path.segments();

        Object current;
        int startIndex;

        if ("ctx".equals(segments.get(0))) {
            if (segments.size() >= 2 && "_source".equals(segments.get(1))) {
                current = document;
                startIndex = 2;
            } else {
                throw new IllegalStateException("Only ctx._source is supported for assignment: " + path);
            }
        } else if (hasVariable(segments.get(0))) {
            current = getVariable(segments.get(0));
            startIndex = 1;
        } else {
            current = variables.computeIfAbsent(segments.get(0), k -> new LinkedHashMap<String, Object>());
            startIndex = 1;
        }

        if (startIndex >= segments.size()) {
            throw new IllegalStateException("Cannot assign directly to ctx._source root: " + path);
        }

        for (int i = startIndex; i < segments.size() - 1; i++) {
            String segment = segments.get(i);

            if (!(current instanceof Map<?, ?>)) {
                throw new IllegalStateException("Path segment is not a map: " + segment);
            }

            Map<String, Object> map = (Map<String, Object>) current;
            current = map.computeIfAbsent(segment, k -> new LinkedHashMap<String, Object>());
        }

        String last = segments.get(segments.size() - 1);

        if (!(current instanceof Map<?, ?>)) {
            throw new IllegalStateException("Cannot assign into non-map path: " + path);
        }

        ((Map<String, Object>) current).put(last, value);
    }

    public Object resolveRoot(String name) {

        if ("ctx".equals(name)) {
            Map<String,Object> wrapper = new HashMap<>();
            wrapper.put("_source", document);
            return wrapper;
        }

        return variables.get(name);
    }
}
