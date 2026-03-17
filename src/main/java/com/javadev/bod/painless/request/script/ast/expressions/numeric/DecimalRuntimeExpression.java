package com.javadev.bod.painless.request.script.ast.expressions.numeric;

import com.javadev.bod.painless.request.ExecutionContext;
import com.javadev.bod.painless.request.script.ast.expressions.Expression;
import com.javadev.bod.painless.request.script.ast.nodes.literal.DecimalLiteralNode;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Runtime expression representing a decimal literal value.
 *
 * <p>This expression is produced during AST → runtime compilation
 * from {@link DecimalLiteralNode DecimalLiteralNode}.</p>
 *
 * <h2>Example</h2>
 *
 * Source:
 * <pre>
 * 3.14
 * </pre>
 *
 * Runtime evaluation:
 * <pre>
 * evaluate(ctx) -> BigDecimal("3.14")
 * </pre>
 *
 * <p>{@link BigDecimal} is used to preserve exact numeric precision
 * and avoid floating point rounding errors.</p>
 */
@Getter
public class DecimalRuntimeExpression implements Expression {

    private final BigDecimal value;

    public DecimalRuntimeExpression(BigDecimal value) {
        this.value = value;
    }

    @Override
    public Object evaluate(ExecutionContext ctx) {
        return value;
    }

    @Override
    public String toString() {
        return "DecimalRuntimeExpression{" +
                "value=" + value +
                '}';
    }
}
