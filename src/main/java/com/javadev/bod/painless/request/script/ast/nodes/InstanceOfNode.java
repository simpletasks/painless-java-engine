package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>{@code
 *
 * x instanceof String
 * ctx._source.tags instanceof List
 * params.payload instanceof Map
 *
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InstanceOfNode implements ExpressionNode {

    private ExpressionNode expression;
    private String typeName;

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitInstanceOf(this);
    }
}