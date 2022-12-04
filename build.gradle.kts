plugins {
    kotlin("jvm")
}

allprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    group = "io.runebox"
    version = "0.1.0"

    repositories {
        mavenLocal()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }

    dependencies {
        implementation(kotlin("stdlib"))
        implementation(kotlin("reflect"))
        testImplementation(kotlin("test"))
    }
}