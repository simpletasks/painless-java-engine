package com.javadev.bod.painless.request.script.ast.expressions;

import com.javadev.bod.painless.request.ExecutionContext;

import java.util.List;

/**
 * Runtime expression that invokes a named script function and returns its result.
 *
 * <p>This expression represents a function call such as:
 *
 * <pre>
 * nowInstant()
 * formatUtcNow("yyyy-MM-dd")
 * </pre>
 *
 * <p>Evaluation flow:
 * <ol>
 *   <li>Resolve the function by {@code functionName}</li>
 *   <li>Evaluate argument expressions from {@code args} as needed</li>
 *   <li>Delegate execution to {@link com.javadev.bod.painless.request.script.ScriptFunctions ScripFunctions} available through
 *       {@link ExecutionContext#functions()}</li>
 *   <li>Return the produced result</li>
 * </ol>
 *
 * <h3>Supported functions in the current implementation</h3>
 * <ul>
 *   <li>{@code nowInstant()} - returns the current instant</li>
 *   <li>{@code formatUtcNow(pattern)} - formats the current UTC timestamp using the given pattern</li>
 * </ul>
 *
 * <h3>Input</h3>
 * <ul>
 *   <li>A function name, for example {@code "nowInstant"} or {@code "formatUtcNow"}</li>
 *   <li>A list of argument expressions</li>
 *   <li>An {@link ExecutionContext} providing access to {@link com.javadev.bod.painless.request.script.ScriptFunctions}</li>
 * </ul>
 *
 * <h3>Output</h3>
 * <ul>
 *   <li>The return value of the invoked function</li>
 * </ul>
 *
 * <h3>Example 1 - no arguments</h3>
 *
 * <pre>
 * expression:
 * └── FunctionCallExpression
 *     ├── functionName: nowInstant
 *     └── args: []
 *
 * evaluate() result:
 * └── 2026-03-15T09:30:00Z
 * </pre>
 *
 * <h3>Example 2 - one argument</h3>
 *
 * <pre>
 * expression:
 * └── FunctionCallExpression
 *     ├── functionName: formatUtcNow
 *     └── args
 *         └── StringLiteralExpression("yyyy-MM-dd")
 *
 * evaluate() result:
 * └── "2026-03-15"
 * </pre>
 *
 * <h3>AST shape</h3>
 *
 * <pre>
 * FunctionCallExpression
 * ├── functionName: formatUtcNow
 * └── args
 *     └── StringLiteralExpression("yyyy-MM-dd")
 * </pre>
 *
 * <p>This expression is a leaf only when it has no child expressions, that is,
 * when {@code args} is empty. If one or more argument expressions are present,
 * it is not a leaf because it contains child nodes that must be evaluated first.
 *
 * @throws IllegalArgumentException if the function name is not supported
 * @throws IllegalArgumentException if the provided argument count is invalid
 */
public class FunctionCallExpression implements Expression {

    private final String functionName;
    private final List<Expression> args;

    public FunctionCallExpression(String functionName, List<Expression> args) {
        this.functionName = functionName;
        this.args = args;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return switch (functionName) {
            case "nowInstant" -> ctx.functions().nowInstant();
            case "formatUtcNow" -> {
                if (args.size() != 1) {
                    throw new IllegalArgumentException("formatUtcNow(pattern) expects 1 arg");
                }
                Object arg0 = args.get(0).evaluate(ctx);
                yield ctx.functions().formatUtcNow(String.valueOf(arg0));
            }
            default -> throw new IllegalArgumentException("Unsupported function: " + functionName);
        };
    }
}
