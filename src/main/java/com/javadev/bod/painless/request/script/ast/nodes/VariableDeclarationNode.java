package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class VariableDeclarationNode implements StatementNode {
    private final String declaredType;
    private final String variableName;
    private final ExpressionNode initializer;

    public VariableDeclarationNode(String declaredType, String variableName, ExpressionNode initializer) {
        this.declaredType = declaredType;
        this.variableName = variableName;
        this.initializer = initializer;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitVariableDeclaration(this);
    }

    @Override
    public String toString() {
        return "VariableDeclarationNode{" +
                "declaredType='" + declaredType + '\'' +
                ", variableName='" + variableName + '\'' +
                ", initializer=" + initializer +
                '}';
    }
}
