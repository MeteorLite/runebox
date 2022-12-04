plugins {
    id("de.fayard.refreshVersions") version "0.51.0"
}

rootProject.name = "runebox"

/**
 * Internal ASM/Bytecode Modules
 */
include(":asm")
include(":deobfuscator")
include(":mapper")

include(":logger")