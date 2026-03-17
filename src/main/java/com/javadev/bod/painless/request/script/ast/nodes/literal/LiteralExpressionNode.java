package com.javadev.bod.painless.request.script.ast.nodes.literal;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.ExpressionNode;

public class LiteralExpressionNode implements ExpressionNode {
    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }
}
