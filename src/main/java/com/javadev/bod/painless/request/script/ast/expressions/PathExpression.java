package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.PathRef;

/**
 * Runtime expression that resolves a value through a {@link PathRef} from the
 * {@link ExecutionContext}.
 *
 * <p>It supports access through the built-in root {@code ctx._source} as well as
 * paths starting from an existing script variable.
 *
 * <h3>Examples of input expressions</h3>
 *
 * <pre>
 * ctx._source.status
 * ctx._source.meta.timestamp
 * order.customer.name
 * </pre>
 *
 * <p>During evaluation it calls:
 *
 * <pre>
 * ctx.getPathValue(path)
 * </pre>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A {@link PathRef} containing path segments, for example
 *       {@code ["ctx", "_source", "status"]}</li>
 *   <li>An {@link ExecutionContext} able to resolve the root object and traverse maps</li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>The value found at the given path</li>
 *   <li>{@code null} if the path does not exist or cannot be resolved</li>
 * </ul>
 *
 * <h3>Example</h3>
 *
 * <pre>
 * document:
 * └── status = "READY"
 *
 * expression:
 * └── PathExpression(PathRef["ctx", "_source", "status"])
 *
 * result:
 * └── "READY"
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * PathExpression
 * └── path: ctx._source.status
 * </pre>
 *
 * <p>If the whole path is treated as one compact {@link PathRef} object,
 * this expression is a leaf in the AST.
 */
public class PathExpression implements Expression {

    private final PathRef path;

    public PathExpression(PathRef path) {
        this.path = path;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return ctx.getPathValue(path);
    }
}
