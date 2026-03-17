package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class IfStatementNode implements StatementNode {
    private final ExpressionNode condition;
    private final StatementNode thenBranch;
    private final StatementNode elseBranch;

    public IfStatementNode(ExpressionNode condition, StatementNode thenBranch, StatementNode elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitIfStatement(this);
    }
    @Override
    public String toString() {
        return "IfStatementNode{" +
                "condition=" + condition +
                ", thenBranch=" + thenBranch +
                ", elseBranch=" + elseBranch +
                '}';
    }
}
