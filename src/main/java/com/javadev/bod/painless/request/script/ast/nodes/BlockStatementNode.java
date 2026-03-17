package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class BlockStatementNode implements StatementNode {
    private final List<StatementNode> statements;

    public BlockStatementNode(List<StatementNode> statements) {
        this.statements = statements == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(statements));
    }

    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitBlockStatement(this);
    }

    @Override
    public String toString() {
        return "BlockStatementNode{statements=" + statements + "}";
    }
}
