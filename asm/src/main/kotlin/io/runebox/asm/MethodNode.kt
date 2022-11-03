package io.runebox.asm

import io.runebox.asm.cfg.FlowGraph
import io.runebox.asm.codegen.CodeGenerator
import io.runebox.asm.editor.MethodEditor
import io.runebox.asm.file.Method
import io.runebox.asm.tree.TreeVisitor
import io.runebox.asm.util.field
import io.runebox.asm.util.nullField
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

typealias ExecutionGraph = FlowGraph

internal fun MethodNode.init(owner: ClassNode) {
    this.owner = owner
}

var MethodNode.owner: ClassNode by field()
val MethodNode.pool get() = owner.pool

var MethodNode.info: Method by field()
var MethodNode.editor: MethodEditor by field()

private var MethodNode.executionGraph: ExecutionGraph? by nullField()

fun MethodNode.execution(): ExecutionGraph {
    if(executionGraph == null) {
        executionGraph = ExecutionGraph(editor)
        executionGraph!!.initialize()
    }
    return executionGraph!!
}

fun MethodNode.execution(visitor: ExprVisitor) {
    this.execution().visit(visitor)
}

fun MethodNode.commit() {
    commitExecutionGraph(false)
    editor.commit()
}

fun MethodNode.commitExecutionGraph(simplify: Boolean = false) {
    if(executionGraph != null) {
        val codegen = CodeGenerator(editor)

        if(simplify) {
            codegen.replacePhis(executionGraph)
            codegen.simplifyControlFlow(executionGraph)
        }

        editor.clearCode()
        executionGraph!!.visit(codegen)
        executionGraph = null
    }
}