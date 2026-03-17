package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;

public class EmptyMapLiteralNode implements ExpressionNode {

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitEmptyMapLiteral(this);
    }

    @Override
    public String toString() {
        return "EmptyMapLiteralNode{}";
    }
}
