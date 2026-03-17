package com.javadev.bod.painless.request.script.ast.expressions;


import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.nodes.UpdateOperator;

/**
 * Runtime expression that applies a prefix or postfix update operator to a numeric variable.
 *
 * <p>Typical supported operators are:
 * <ul>
 *   <li>{@code ++x} / {@code x++}</li>
 *   <li>{@code --x} / {@code x--}</li>
 * </ul>
 *
 * <p>The expression:
 * <ul>
 *   <li>reads the current variable value</li>
 *   <li>checks that the current value is numeric</li>
 *   <li>computes the updated value</li>
 *   <li>writes the updated value back into the {@link ExecutionContext}</li>
 *   <li>returns either the prefix or postfix result</li>
 * </ul>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A variable name, for example {@code "count"}</li>
 *   <li>An operator, for example {@code INCREMENT} or {@code DECREMENT}</li>
 *   <li>A {@code prefix} flag</li>
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
 * variables:
 * └── count = 10
 *
 * expression:
 * └── ++count
 *
 * evaluate() result:
 * └── 11
 *
 * state after evaluate():
 * └── count = 11
 * </pre>
 *
 * <h3>Example 2 - postfix increment</h3>
 *
 * <pre>
 * variables:
 * └── count = 10
 *
 * expression:
 * └── count++
 *
 * evaluate() result:
 * └── 10
 *
 * state after evaluate():
 * └── count = 11
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * VariableUpdateExpression
 * ├── variableName: count
 * ├── operator: INCREMENT
 * └── prefix: false
 * </pre>
 *
 * <p>In this runtime model the expression can be treated as a leaf because it has no
 * child expressions. In a more classical AST it is usually represented as a non-leaf
 * unary expression whose operand is a variable reference node.
 *
 * @throws IllegalArgumentException if the target variable is not numeric
 */
public class VariableUpdateExpression implements Expression {
    private final String variableName;
    private final UpdateOperator operator;
    private final boolean prefix;

    public VariableUpdateExpression(String variableName, UpdateOperator operator, boolean prefix) {
        this.variableName = variableName;
        this.operator = operator;
        this.prefix = prefix;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        Object current = ctx.getVariable(variableName);

        if (!(current instanceof Number number)) {
            throw new IllegalArgumentException("Update target must be numeric variable: " + variableName);
        }

        long oldValue = number.longValue();
        long newValue = switch (operator) {
            case INCREMENT -> oldValue + 1;
            case DECREMENT -> oldValue - 1;
        };

        ctx.setVariable(variableName, newValue);

        return prefix ? newValue : oldValue;
    }
}
