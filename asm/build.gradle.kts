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
    implementation("com.google.code.gson:gson:_")
    implementation("org.apache.commons:commons-lang3:_")
    implementation("org.glassfish.jaxb:jaxb-runtime:_")
    implementation("javax.xml.bind:jaxb-api:_")
    implementation("javax.activation:activation:_")
    implementation("commons-io:commons-io:_")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    disableAutoTargetJvm()

}

tasks {
    compileJava {
        options.compilerArgs.addAll(listOf(
            "--add-exports", "java.base/sun.invoke.util=ALL-UNNAMED"
        ))
    }
}