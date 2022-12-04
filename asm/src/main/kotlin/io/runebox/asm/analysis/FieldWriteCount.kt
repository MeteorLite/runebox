package io.runebox.asm.analysis

enum class FieldWriteCount {
    NEVER,
    EXACTLY_ONCE,
    ONCE_OR_MORE
}
