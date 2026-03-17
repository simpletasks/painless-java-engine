package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.PathRef;
import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

@Getter
public class PathTargetNode implements AssignableNode {
    private final PathRef path;

    public PathTargetNode(PathRef path) {
        this.path = path;
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitPathTarget(this);
    }

    @Override
    public String toString() {
        return "PathTargetNode{" +
                "path=" + path +
                '}';
    }
}
