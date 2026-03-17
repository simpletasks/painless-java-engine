package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>{@code
 *
 * x += 1
 * x -= 2
 * x *= 3
 * x /= 4
 * map["a"] += 1
 * ctx._source.count += 1
 *
 * }</pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CompoundAssignmentNode implements StatementNode {

    private AssignableNode target;
    private CompoundAssignmentOperator operator;
    private ExpressionNode value;

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitCompoundAssignmentStatement(this);
    }
}