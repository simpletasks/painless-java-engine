package com.javadev.bod.painless.request.script.ast.nodes.initialization;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>{@code
 *
 * (String) x
 * (Map) params.payload
 * (List) ctx._source.tags
 *
 * } </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CastNode implements ExpressionNode {

    private String targetType;
    private ExpressionNode expression;

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitCast(this);
    }
}