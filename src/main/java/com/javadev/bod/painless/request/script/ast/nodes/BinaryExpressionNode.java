package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.BinaryOperator;
import lombok.Getter;

/**
 * <pre>{@code
 *
 * ==
 * !=
 * only in IF satements
 *
 * all other use cases
 * a + b
 * a - b
 * a * b
 * a / b
 * a % b
 * a && b
 * a || b
 * a < b
 * a <= b
 * a > b
 * a >= b
 *
 * }</pre>
 */
@Getter
public class BinaryExpressionNode implements ExpressionNode {
    private final ExpressionNode left;
    private final BinaryOperator operator;
    private final ExpressionNode right;

    public BinaryExpressionNode(ExpressionNode left, BinaryOperator operator, ExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBinaryExpression(this);
    }
    @Override
    public String toString() {
        return "BinaryExpressionNode{" +
                "left=" + left +
                ", operator=" + operator +
                ", right=" + right +
                '}';
    }
}
