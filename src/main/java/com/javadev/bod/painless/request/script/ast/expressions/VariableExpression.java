package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

/**
 * Runtime expression that resolves a variable value from the {@link ExecutionContext}.
 *
 * <p>This expression represents a plain variable reference such as:
 *
 * <pre>
 * count
 * status
 * tmpValue
 * </pre>
 *
 * <p>During evaluation it calls:
 *
 * <pre>
 * ctx.getVariable(variableName)
 * </pre>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A variable name, for example {@code "count"}</li>
 *   <li>An {@link ExecutionContext} containing script variables</li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>The value stored under the given variable name</li>
 *   <li>{@code null} if the variable is not defined</li>
 * </ul>
 *
 * <h3>Example</h3>
 *
 * <pre>
 * variables:
 * └── count = 10
 *
 * expression:
 * └── VariableExpression("count")
 *
 * result:
 * └── 10
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * VariableExpression
 * └── variableName: count
 * </pre>
 *
 * <p>In AST terms this is a typical leaf expression because it has no child expressions.
 */
public class VariableExpression implements Expression {

    private final String variableName;

    public VariableExpression(String variableName) {
        this.variableName = variableName;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return ctx.getVariable(variableName);
    }
}
