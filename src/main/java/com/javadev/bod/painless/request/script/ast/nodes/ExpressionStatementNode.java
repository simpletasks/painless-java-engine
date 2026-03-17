package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class ExpressionStatementNode implements StatementNode {
    private final ExpressionNode expression;

    public ExpressionStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitExpressionStatement(this);
    }
    @Override
    public String toString() {
        return "ExpressionStatementNode{" +
                "expression=" + expression +
                '}';
    }
}
