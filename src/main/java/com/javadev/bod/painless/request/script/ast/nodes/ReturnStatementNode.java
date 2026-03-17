package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class ReturnStatementNode implements StatementNode {
    private final ExpressionNode expression;

    public ReturnStatementNode(ExpressionNode expression) {
        this.expression = expression;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitReturnStatement(this);
    }

    @Override
    public String toString() {
        return "ReturnStatementNode{" +
                "expression=" + expression +
                '}';
    }
}
