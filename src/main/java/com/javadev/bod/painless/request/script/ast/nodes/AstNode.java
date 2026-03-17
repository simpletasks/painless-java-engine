package com.javadev.bod.painless.request.script.ast.nodes;

import com.javadev.bod.painless.request.script.ast.AstVisitor;

public interface AstNode {
    <T> T accept(AstVisitor<T> visitor);
}
