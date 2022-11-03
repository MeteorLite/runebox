package io.runebox.asm.tree

import io.runebox.asm.util.field
import org.objectweb.asm.tree.ParameterNode

var ParameterNode.mappedName: String? by field { it.name }