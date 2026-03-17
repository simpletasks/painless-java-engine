package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.script.ast.nodes.UpdateOperator;

/**
 * Runtime expression that applies a prefix or postfix update operator to a numeric value
 * located at the given {@link PathRef}.
 *
 * <p>Typical supported operators are:
 * <ul>
 *   <li>{@code ++}</li>
 *   <li>{@code --}</li>
 * </ul>
 *
 * <p>The expression:
 * <ul>
 *   <li>reads the current value from the path</li>
 *   <li>checks that the current value is numeric</li>
 *   <li>computes the updated value</li>
 *   <li>writes the updated value back into the {@link ExecutionContext}</li>
 *   <li>returns either the prefix or postfix result</li>
 * </ul>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A path, for example {@code ctx._source.count}</li>
 *   <li>An operator, for example {@code INCREMENT} or {@code DECREMENT}</li>
 *   <li>A {@code prefix} flag:
 *     <ul>
 *       <li>{@code true} for {@code ++x} / {@code --x}</li>
 *       <li>{@code false} for {@code x++} / {@code x--}</li>
 *     </ul>
 *   </li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>For prefix form: the new value</li>
 *   <li>For postfix form: the old value</li>
 * </ul>
 *
 * <h3>Example 1 - prefix increment</h3>
 *
 * <pre>
 * document:
 * └── count = 10
 *
 * expression:
 * └── ++ctx._source.count
 *
 * evaluate() result:
 * └── 11
 *
 * state after evaluate():
 * └── ctx._source.count = 11
 * </pre>
 *
 * <h3>Example 2 - postfix increment</h3>
 *
 * <pre>
 * document:
 * └── count = 10
 *
 * expression:
 * └── ctx._source.count++
 *
 * evaluate() result:
 * └── 10
 *
 * state after evaluate():
 * └── ctx._source.count = 11
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * PathUpdateExpression
 * ├── path: ctx._source.count
 * ├── operator: INCREMENT
 * └── prefix: true
 * </pre>
 *
 * <p>In this runtime model the expression can be treated as a leaf because the update
 * operation is already compiled into a single executable unit. In a more classical AST
 * it is usually represented as a non-leaf unary expression.
 *
 * @throws IllegalArgumentException if the current path value is not numeric
 */
public class PathUpdateExpression implements Expression {
    private final PathRef path;
    private final UpdateOperator operator;
    private final boolean prefix;

    public PathUpdateExpression(PathRef path, UpdateOperator operator, boolean prefix) {
        this.path = path;
        this.operator = operator;
        this.prefix = prefix;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object current = ctx.getPathValue(path);

        if (!(current instanceof Number number)) {
            throw new IllegalArgumentException("Update target must be numeric path: " + path);
        }

        long oldValue = number.longValue();
        long newValue = switch (operator) {
            case INCREMENT -> oldValue + 1;
            case DECREMENT -> oldValue - 1;
        };

        ctx.setPathValue(path, newValue);

        return prefix ? newValue : oldValue;
    }
}
