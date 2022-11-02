package io.runebox.asm.tree

import kotlinx.serialization.Serializable

@Serializable
data class ObfuscatedNames(
    val mappings: Map<String, String>
)