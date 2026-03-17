package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

/**
 * AST node representing an integer literal.
 *
 * <p>This node corresponds to numeric literals without a fractional part
 * in the source code.</p>
 *
 * <h2>Example</h2>
 *
 * Source:
 * <pre>
 * 42
 * </pre>
 *
 * AST:
 * <pre>
 * IntegerLiteralNode(42)
 * </pre>
 *
 * <p>The value is stored as {@code long} to match the default integer
 * representation used in the interpreter runtime.</p>
 */
@Getter
public class IntegerLiteralNode extends NumericLiteralNode {

    private final long value;

    public IntegerLiteralNode(long value) {
        super(value);
        this.value = value;
    }

    public Long getValue() {
        return value;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIntegerLiteral(this);
    }
    @Override
    public String toString() {
        return "IntegerLiteralNode{" +
                "value=" + value +
                '}';
    }
}
