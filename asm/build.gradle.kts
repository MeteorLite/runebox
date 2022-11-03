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
    api("org.jetbrains.kotlinx:kotlinx-serialization-json:_")
    implementation("com.squareup:kotlinpoet:_")
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    disableAutoTargetJvm()

}

tasks {
}