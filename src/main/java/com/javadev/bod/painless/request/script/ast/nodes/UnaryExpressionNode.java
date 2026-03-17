package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

/**
 * AST node representing a unary expression.
 *
 * <p>Examples:</p>
 *
 * <pre>
 * !flag
 * -value
 * +amount
 * </pre>
 */
@Getter
public class UnaryExpressionNode implements ExpressionNode {

    private final UnaryOperator operator;
    private final ExpressionNode operand;

    public UnaryExpressionNode(UnaryOperator operator, ExpressionNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }

    @Override
    public String toString() {
        return "UnaryExpressionNode{" +
                "operator=" + operator +
                ", operand=" + operand +
                '}';
    }
}
