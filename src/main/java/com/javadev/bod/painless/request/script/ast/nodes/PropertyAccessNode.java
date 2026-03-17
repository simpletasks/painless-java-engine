package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>{@code
 *
 * obj.field
 * ctx._source.status
 * params.user.name
 *
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyAccessNode implements ExpressionNode {
    private ExpressionNode target;
    private String propertyName;

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitPropertyAccess(this);
    }
}
