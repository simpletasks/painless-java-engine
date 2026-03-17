package com.javadev.bod.painless.request.script.ast.expressions.numeric;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;
import com.javadev.bod.painless.request.script.ast.nodes.literal.IntegerLiteralNode;
import lombok.Getter;

/**
 * Runtime expression representing an integer literal value.
 *
 * <p>This expression is produced during AST → runtime compilation
 * from {@link IntegerLiteralNode IntegerLiteralNode}.</p>
 *
 * <h2>Example</h2>
 *
 * Source:
 * <pre>
 * 42
 * </pre>
 *
 * Runtime evaluation:
 * <pre>
 * evaluate(ctx) -> 42L
 * </pre>
 *
 * <p>The value is stored as {@code long} which is the default integer
 * representation used by the interpreter runtime.</p>
 */
@Getter
public class IntegerRuntimeExpression implements Expression {

    private final long value;

    public IntegerRuntimeExpression(long value) {
        this.value = value;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return value;
    }

    @Override
    public String toString() {
        return "IntegerRuntimeExpression{" +
                "value=" + value +
                '}';
    }
}
