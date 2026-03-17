package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class PathReferenceNode implements ExpressionNode {
    private final PathRef path;

    public PathReferenceNode(PathRef path) {
        this.path = path;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitPathReference(this);
    }

    @Override
    public String toString() {
        return "PathReferenceNode{" +
                "path=" + path +
                '}';
    }
}
