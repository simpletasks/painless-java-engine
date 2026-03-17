package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Runtime expression that invokes a method on the result of another expression.
 *
 * <p>This expression represents an instance method call such as:
 *
 * <pre>
 * customer.getName()
 * order.calculateTotal()
 * text.substring(0, 3)
 * </pre>
 *
 * <p>Evaluation flow:
 * <ol>
 *   <li>Evaluate the {@code target} expression</li>
 *   <li>Evaluate all argument expressions from {@code args}</li>
 *   <li>Use reflection to find a public method on the target object's class
 *       matching {@code methodName} and argument count</li>
 *   <li>Invoke the method and return its result</li>
 * </ol>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A target expression producing the receiver object</li>
 *   <li>A method name, for example {@code "getName"} or {@code "substring"}</li>
 *   <li>A list of argument expressions</li>
 *   <li>An {@link ExecutionContext} used to evaluate the target and arguments</li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>The return value of the invoked method</li>
 * </ul>
 *
 * <h3>Example 1 - no method arguments</h3>
 *
 * <pre>
 * ExecutionContext
 * └── variables
 *     └── customer
 *         └── Customer instance
 *             ├── id: "c-100"
 *             ├── name: "Ana"
 *             └── active: true
 *
 *
 * expression:
 * └── MethodCallExpression
 *     ├── target
 *     │   └── VariableExpression("customer")
 *     ├── methodName: getName
 *     └── args: []
 *
 *
 * evaluation flow:
 *
 * VariableExpression("customer")
 * └── resolves from ExecutionContext.variables
 *
 * ExecutionContext.variables
 * └── customer
 *     └── Customer instance
 *
 *
 * method invocation:
 *
 * customer.getName()
 * └── returns "Ana"
 *
 *
 * evaluate() result:
 * └── "Ana"
 * </pre>
 *
 * <h3>Example 2 - with method arguments</h3>
 *
 * <pre>
 * variables:
 * └── text = "abcdef"
 *
 * expression:
 * └── MethodCallExpression
 *     ├── target
 *     │   └── VariableExpression("text")
 *     ├── methodName: substring
 *     └── args
 *         ├── IntegerLiteralExpression(0)
 *         └── IntegerLiteralExpression(3)
 *
 * evaluate() result:
 * └── "abc"
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * MethodCallExpression
 * ├── target
 * │   └── VariableExpression("text")
 * ├── methodName: substring
 * └── args
 *     ├── IntegerLiteralExpression(0)
 *     └── IntegerLiteralExpression(3)
 * </pre>
 *
 * <p>This expression is not a leaf in the current design because it always contains
 * at least the child expression {@code target}. If arguments are present, they are
 * additional child expressions.
 *
 * @throws RuntimeException if method lookup or reflective invocation fails
 */
public class MethodCallExpression implements Expression {

    private final Expression target;
    private final String methodName;
    private final List<Expression> args;

    public MethodCallExpression(Expression target,
                                String methodName,
                                List<Expression> args) {
        this.target = target;
        this.methodName = methodName;
        this.args = args;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {

        Object obj = target.evaluate(ctx);

        List<Object> values = new ArrayList<>();

        for (Expression e : args) {
            values.add(e.evaluate(ctx));
        }

        try {

            Class<?> clazz = obj.getClass();

            for (Method m : clazz.getMethods()) {

                if (!m.getName().equals(methodName)) {
                    continue;
                }

                if (m.getParameterCount() != values.size()) {
                    continue;
                }

                return m.invoke(obj, values.toArray());
            }

            throw new IllegalArgumentException(
                    "Method not found: " + methodName + " on " + clazz.getName()
            );

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
