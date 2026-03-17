package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>{@code
 *
 * i++
 * ++i
 * i--
 * --i
 *
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateExpressionNode implements ExpressionNode {

    private ExpressionNode target;
    private UpdateOperator operator;
    private boolean prefix;

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitUpdateExpression(this);
    }
}