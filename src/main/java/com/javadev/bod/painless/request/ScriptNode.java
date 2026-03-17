package com.javadev.bod.painless.request;

import com.javadev.bod.painless.request.script.ast.AstVisitor;
import com.javadev.bod.painless.request.script.ast.nodes.AstNode;
import com.javadev.bod.painless.request.script.ast.nodes.StatementNode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Root node of the Abstract Syntax Tree (AST) representing an entire script.
 *
 * <p>This node contains the ordered list of top-level {@link StatementNode statements}
 * that make up the script body. It serves as the entry point for compilation or
 * interpretation of the script.</p>
 *
 * <h2>Design intent</h2>
 *
 * <p>The {@code ScriptNode} represents only the <b>syntactic structure</b> of the script.
 * Script-specific execution semantics are intentionally handled outside the AST,
 * typically in the compiler or the script executor.</p>
 *
 * <p>In particular:</p>
 * <ul>
 *   <li>The script is modeled as a sequence of statements.</li>
 *   <li>A {@code return} statement terminates execution of the entire script.</li>
 *   <li>The runtime handling of {@code return} (for example via a control-flow
 *       exception) is implemented in the execution layer rather than the AST.</li>
 * </ul>
 *
 * <h2>Execution model</h2>
 *
 * <p>During compilation the {@code ScriptNode} is translated into a root runtime
 * command (typically a {@code BlockCommand}) which sequentially executes all
 * statements in the script.</p>
 *
 * <pre>
 * ScriptNode
 *  └─ StatementNode*
 * </pre>
 *
 * <p>Control flow constructs such as {@code return} may interrupt execution
 * before all statements are executed. The mechanism used to propagate this
 * control flow (e.g. throwing a {@code ScriptReturnException}) is handled by
 * the script executor and not by this node.</p>
 *
 * <h2>Why this node exists</h2>
 *
 * <p>Separating the script root from ordinary statements provides a clear
 * boundary between:</p>
 *
 * <ul>
 *   <li>the <b>syntactic representation</b> of the script (AST)</li>
 *   <li>the <b>execution semantics</b> implemented by the interpreter/compiler</li>
 * </ul>
 *
 <p>This separation keeps the AST simple and focused on language structure,
 * while the execution layer handles script lifecycle concerns such as control flow,
 * return value handling, and execution context management.</p>
 *
 * <p>Examples of responsibilities handled outside the AST:</p>
 *
 * <ul>
 *   <li>
 *     <b>Control flow propagation</b><br>
 *     When a {@code return} statement is executed, the runtime may throw a
 *     {@code ScriptReturnException} to immediately terminate script execution.
 *     The AST only represents the {@code return} statement; the propagation
 *     mechanism is implemented in the execution layer.
 *   </li>
 *
 *   <li>
 *     <b>Return value handling</b><br>
 *     After compilation, the {@code ScriptNode} is translated into a root
 *     runtime command (for example a {@code BlockCommand}). The executor
 *     runs this command and captures the returned value if execution is
 *     interrupted by a {@code return}.
 *   </li>
 *
 *   <li>
 *     <b>Execution context management</b><br>
 *     Script variables such as {@code ctx}, {@code params}, or other runtime
 *     objects are provided by the execution environment. The AST only contains
 *     references to these variables, while their actual values are resolved
 *     at runtime using the {@code ExecutionContext}.
 *   </li>
 *
 *   <li>
 *     <b>Script lifecycle</b><br>
 *     The execution layer may perform additional steps such as preparing the
 *     {@code ExecutionContext}, initializing runtime state, executing the
 *     compiled command tree, and extracting the final result of the script.
 *   </li>
 * </ul>
 */
@Getter
public class ScriptNode implements AstNode {
    private final List<StatementNode> statements;

    public ScriptNode(List<StatementNode> statements) {
        this.statements = statements == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(new ArrayList<>(statements));
    }

    @Override
    public String toString() {
        return "ScriptNode{statements=" + statements + '}';
    }

    @Override
    public <T> T accept(AstVisitor<T> visitor) {
        return visitor.visitScript(this);
    }
}
