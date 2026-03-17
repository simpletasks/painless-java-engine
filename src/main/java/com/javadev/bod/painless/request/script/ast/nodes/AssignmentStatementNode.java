package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class AssignmentStatementNode implements StatementNode {
    private final AssignableNode target;
    private final ExpressionNode value;

    public AssignmentStatementNode(AssignableNode target, ExpressionNode value) {
        this.target = target;
        this.value = value;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitAssignmentStatement(this);
    }
    @Override
    public String toString() {
        return "AssignmentStatementNode{" +
                "target=" + target +
                ", value=" + value +
                '}';
    }
}
