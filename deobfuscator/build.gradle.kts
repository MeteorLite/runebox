dependencies {
    implementation(project(":asm"))
    implementation(project(":logger"))
    implementation("org.jetbrains.kotlinx:kotlinx-cli-jvm:_")
}

tasks.register<JavaExec>("download-gamepack") {
    dependsOn(tasks.build)
    group = "tools"
    mainClass.set("io.runebox.deobfuscator.ClientDownloader")
    workingDir = rootProject.projectDir
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("build/workspace/")
}

tasks.register<JavaExec>("deobfuscate-gamepack") {
    group = "tools"
    mainClass.set("io.runebox.deobfuscator.Deobfuscator")
    workingDir = rootProject.projectDir
    classpath = sourceSets["main"].runtimeClasspath
    args = listOf("build/workspace/gamepack.jar", "build/workspace/gamepack-deob.jar")
}