package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * AST node representing a decimal numeric literal.
 *
 * <p>This node corresponds to numeric literals that contain a fractional
 * part in the source code.</p>
 *
 * <h2>Example</h2>
 *
 * Source:
 * <pre>
 * 3.14
 * </pre>
 *
 * AST:
 * <pre>
 * DecimalLiteralNode(3.14)
 * </pre>
 *
 * <p>The value is stored as {@link BigDecimal} to preserve precision and
 * avoid floating point rounding errors.</p>
 */
@Getter
public class DecimalLiteralNode extends NumericLiteralNode {

    private final BigDecimal value;

    public DecimalLiteralNode(BigDecimal value) {
        super(value);
        this.value = value;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitDecimalLiteral(this);
    }

    @Override
    public String toString() {
        return "DecimalLiteralNode{" +
                "value=" + value +
                '}';
    }
}
