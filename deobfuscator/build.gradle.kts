dependencies {
    implementation(project(":asm"))
    implementation(project(":logger"))
}

tasks {
    register<JavaExec>("run") {
        dependsOn(build.get())
        group = "application"
        mainClass.set("io.runebox.deobfuscator.Deobfuscator")
        classpath = sourceSets["main"].runtimeClasspath
    }

    register<Jar>("shadowJar") {
        dependsOn(build.get())
        group = "build"
        archiveClassifier.set("shaded")
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        manifest {
            attributes["Main-Class"] = "io.runebox.deobfuscator.Deobfuscator"
        }
        from(configurations.runtimeClasspath.get().map {
            if(it.isDirectory) it
            else zipTree(it)
        })
        with(jar.get())
    }
}