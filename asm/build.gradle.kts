plugins {
    java
    kotlin("plugin.serialization")
}

dependencies {
    api("org.ow2.asm:asm:_")
    api("org.ow2.asm:asm-commons:_")
    api("org.ow2.asm:asm-util:_")
    api("org.ow2.asm:asm-tree:_")
    api("com.google.guava:guava:_")
    api("com.google.code.gson:gson:_")
    api("org.apache.commons:commons-lang3:_")
    api("org.glassfish.jaxb:jaxb-runtime:_")
    api("javax.xml.bind:jaxb-api:_")
    api("javax.activation:activation:_")
    api("commons-io:commons-io:_")
    api("com.jcraft:jzlib:_")
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    disableAutoTargetJvm()

}

tasks {
    compileJava {
        options.compilerArgs.addAll(listOf(
            "--add-exports", "java.base/sun.invoke.util=ALL-UNNAMED",
            "--add-exports", "java.base/jdk.internal.misc=ALL-UNNAMED"
        ))
    }
}