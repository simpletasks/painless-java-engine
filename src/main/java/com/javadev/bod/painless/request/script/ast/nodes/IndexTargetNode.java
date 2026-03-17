package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class IndexTargetNode implements AssignableNode {
    private final ExpressionNode target;
    private final ExpressionNode index;

    public IndexTargetNode(ExpressionNode target, ExpressionNode index) {
        this.target = target;
        this.index = index;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIndexTarget(this);
    }

    @Override
    public String toString() {
        return "IndexTargetNode{" +
                "target=" + target +
                ", index=" + index +
                '}';
    }
}
