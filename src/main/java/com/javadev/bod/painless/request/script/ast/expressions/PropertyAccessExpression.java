package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.Map;

/**
 * Runtime expression that reads a property from the result of another expression.
 *
 * <p>This expression first evaluates the target expression and then tries to read
 * a property from the resulting object. At the moment only {@link Map}-based
 * access is supported.
 *
 * <h3>Examples of input expressions</h3>
 *
 * <pre>
 * customer.name
 * order.total
 * someMap.value
 * </pre>
 *
 * <p>Semantics:
 * <ol>
 *   <li>Evaluate {@code target}</li>
 *   <li>If the result is {@code null}, return {@code null}</li>
 *   <li>If the result is a {@code Map}, return {@code map.get(propertyName)}</li>
 *   <li>Otherwise throw an exception</li>
 * </ol>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A target expression, for example {@code VariableExpression("customer")}</li>
 *   <li>A property name, for example {@code "name"}</li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>The property value</li>
 *   <li>{@code null} if the target is {@code null} or if the map key does not exist</li>
 * </ul>
 *
 * <h3>Example</h3>
 *
 * <pre>
 * variables:
 * └── customer
 *     └── name = "Ana"
 *
 * expression:
 * └── PropertyAccessExpression
 *     ├── target
 *     │   └── VariableExpression("customer")
 *     └── propertyName: "name"
 *
 * result:
 * └── "Ana"
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * PropertyAccessExpression
 * ├── target
 * │   └── VariableExpression("customer")
 * └── propertyName: name
 * </pre>
 *
 * <p>This is not a leaf expression because it contains a child expression: {@code target}.
 *
 * @throws IllegalArgumentException if the evaluated target is not a {@link Map}
 */
public class PropertyAccessExpression implements Expression {
    private final Expression target;
    private final String propertyName;

    public PropertyAccessExpression(Expression target, String propertyName) {
        this.target = target;
        this.propertyName = propertyName;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object base = target.evaluate(ctx);

        if (base == null) {
            return null;
        }

        if (base instanceof Map<?, ?> map) {
            return map.get(propertyName);
        }

        throw new IllegalArgumentException(
                "Property access is supported only on Map for now. Property: " + propertyName
        );
    }
}
