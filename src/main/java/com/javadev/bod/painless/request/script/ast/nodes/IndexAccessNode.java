package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class IndexAccessNode implements ExpressionNode {
    private final ExpressionNode target;
    private final ExpressionNode index;

    public IndexAccessNode(ExpressionNode target, ExpressionNode index) {
        this.target = target;
        this.index = index;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIndexAccess(this);
    }

    @Override
    public String toString() {
        return "IndexAccessNode{" +
                "target=" + target +
                ", index=" + index +
                '}';
    }
}
