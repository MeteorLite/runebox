package io.runebox.asm

import io.runebox.asm.tree.ArithExpr
import io.runebox.asm.tree.CallStaticExpr
import org.objectweb.asm.tree.MethodNode

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val method = MethodNode()
        method.execution(object : ExprVisitor() {

            // INVOKESTATIC FRAMES
            override fun visitCallStaticExpr(expr: CallStaticExpr) {
                val m = expr.method().name()
            }

            // BINARY MATH OPARATION FRAMES
            override fun visitArithExpr(expr: ArithExpr) {
                val operator = expr.operation()
                val left = expr.left()
                val right = expr.right()
            }
        })
    }
}