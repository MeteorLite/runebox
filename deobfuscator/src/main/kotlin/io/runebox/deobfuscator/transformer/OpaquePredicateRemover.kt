package io.runebox.deobfuscator.transformer

import com.google.common.collect.MultimapBuilder
import io.runebox.asm.matcher.*
import io.runebox.asm.tree.*
import io.runebox.asm.util.InstructionModifier
import io.runebox.asm.utils.Utils
import io.runebox.asm.utils.Utils.isReturn
import io.runebox.deobfuscator.Deobfuscator.isObfuscatedName
import io.runebox.deobfuscator.Transformer
import io.runebox.deobfuscator.util.iterate
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type
import org.objectweb.asm.Type.*
import org.objectweb.asm.tree.*
import org.tinylog.kotlin.Logger
import java.lang.reflect.Modifier

class OpaquePredicateRemover : Transformer {

    private var checkCount = 0
    private var argCount = 0

    override fun run(pool: ClassPool) {
        removeOpaqueChecks(pool)
        removeOpaqueArgs(pool)
        Logger.info("Removed $checkCount opaque check and $argCount opaque args.")
    }

    private fun removeOpaqueChecks(pool: ClassPool) {
        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                val modifier = InstructionModifier()

                val insns = method.instructions.iterator()
                while(insns.hasNext()) {
                    val insn = insns.next()

                    val matcher = OPAQUE_CHECK_PATTERN.matcher(insn)
                    if(matcher.find()) {
                        val matchedInsns = matcher.getCapturedInstructions("all")
                        val load = matcher.getCapturedInstruction("load") as? VarInsnNode ?: continue
                        val jump = matcher.getCapturedInstruction("jump") as? JumpInsnNode ?: continue
                        val goto = JumpInsnNode(GOTO, LabelNode(jump.label.label))

                        if(load.`var` == method.lastArgIndex) {
                            modifier.append(matchedInsns.last(), goto)
                            modifier.removeAll(matchedInsns)
                            checkCount++
                            continue
                        }
                    }
                }

                modifier.apply(method)
            }
        }
    }

    private fun removeOpaqueArgs(pool: ClassPool) {
        val rootMethods = hashSetOf<String>()
        val opaqueMethods = MultimapBuilder.hashKeys().arrayListValues().build<String, MethodNode>()
        val opaqueMethodMap = opaqueMethods.asMap()

        pool.classes.forEach { cls ->
            val superClasses = pool.findSupers(cls)
            cls.methods.forEach methodLoop@ { method ->
                if(superClasses.none { it.getMethod(method.name, method.desc) != null }) {
                    rootMethods.add(method.id)
                }
            }
        }

        pool.classes.forEach { cls ->
            cls.methods.forEach methodLoop@ { method ->
                val id = pool.findOverride(method.owner.name, method.name, method.desc, rootMethods) ?: return@methodLoop
                opaqueMethods.put(id, method)
            }
        }

        val itr = opaqueMethods.asMap().iterator()
        for((_, method) in itr) {
            if(method.any { !it.hasOpaqueArg() }) {
                itr.remove()
            }
        }

        pool.classes.flatMap { it.methods }.forEach { method ->
            val insns = method.instructions
            for(insn in insns) {
                if (insn !is MethodInsnNode) continue
                val id = pool.findOverride(insn.owner, insn.name, insn.desc, opaqueMethodMap.keys) ?: continue
                if(!Utils.isInteger(insn.previous)) {
                    opaqueMethodMap.remove(id)
                }
            }
        }

        opaqueMethods.values().forEach { method ->
            method.desc = method.desc.dropLastArg()
            argCount++
        }

        pool.classes.flatMap { it.methods }.forEach { method ->
            val insns = method.instructions
            for(insn in insns) {
                if(insn !is MethodInsnNode) continue
                pool.findOverride(insn.owner, insn.name, insn.desc, opaqueMethodMap.keys)?.also {
                    insn.desc = insn.desc.dropLastArg()
                    insns.remove(insn.previous)
                }
            }
        }
    }

    private val MethodNode.lastArgIndex: Int get() {
        val offset = if(Modifier.isStatic(access)) 1 else 0
        return (getArgumentsAndReturnSizes(desc) shr 2) - offset - 1
    }

    private fun MethodNode.hasOpaqueArg(): Boolean {
        if(argumentTypes.isEmpty()) return false
        val lastArg = argumentTypes.last()
        if(lastArg !in arrayOf(BYTE_TYPE, SHORT_TYPE, INT_TYPE)) return false
        if(isAbstract()) return true
        instructions.forEach { insn ->
            if(insn !is VarInsnNode) return@forEach
            if(insn.`var` == lastArgIndex) return false
        }
        return true
    }

    private fun String.dropLastArg(): String {
        val type = Type.getMethodType(this)
        return Type.getMethodDescriptor(type.returnType, *type.argumentTypes.copyOf(type.argumentTypes.size - 1))
    }

    private val ClassPool.classNames get() = allClasses.associateBy { it.name }

    private fun ClassPool.findSupers(cls: ClassNode): Collection<ClassNode> {
        return cls.interfaces.plus(cls.superName).mapNotNull { classNames[it] }.flatMap { findSupers(it).plus(it) }
    }

    private fun ClassPool.findOverride(owner: String, name: String, desc: String, methods: Set<String>): String? {
        val identifier = "$owner.$name$desc"
        if(identifier in methods) return identifier
        if(name.startsWith("<init>")) return null
        val cls = classNames[owner] ?: return null
        for(sup in findSupers(cls)) {
            return findOverride(sup.name, name, desc, methods) ?: continue
        }
        return null
    }

    companion object {

        private val OPAQUE_CHECK_PATTERN = InstructionPattern(
            OrStep(
                // Opaque Predicate Return Check
                MultiStep(
                    CapturingStep(OpcodeStep({it is VarInsnNode}, ILOAD), "load"),
                    PushIntStep(),
                    CapturingStep(CheckStep { it is JumpInsnNode && it.opcode != GOTO }, "jump"),
                    CheckStep { isReturn(it) }
                ),
                // Opaque Predicate Exception throwing Check
                MultiStep(
                    CapturingStep(OpcodeStep({ it is VarInsnNode }, ILOAD), "load"),
                    PushIntStep(),
                    CapturingStep(CheckStep { it is JumpInsnNode && it.opcode != GOTO }, "jump"),
                    OpcodeStep({ it is TypeInsnNode && it.desc == "java/lang/IllegalStateException" }, NEW),
                    OpcodeStep(DUP),
                    OpcodeStep(INVOKESPECIAL),
                    OpcodeStep(ATHROW),
                )
            )
        )
    }
}