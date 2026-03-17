package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

/**
 * Base AST node for all numeric literal expressions.
 *
 * <p>This node is used as a common parent for integer and decimal literals.
 * It allows the parser, AST visitors, and runtime layers to treat numeric
 * literals uniformly when needed.</p>
 */
@Getter
public abstract class NumericLiteralNode extends LiteralExpressionNode {

    private final Number value;

    protected NumericLiteralNode(Number value) {
        this.value = value;
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitNumericalLiteral(this);
    }
    /**
     * Returns the numeric value as stored by the concrete literal node.
     */
    public Number getNumericValue() {
        return value;
    }
}
