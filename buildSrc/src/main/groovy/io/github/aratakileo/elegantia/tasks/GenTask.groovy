package io.github.aratakileo.elegantia.tasks

import io.github.aratakileo.elegantia.Constants
import org.gradle.api.file.Directory
import org.gradle.api.tasks.Internal
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

abstract class GenTask extends ElegantiaTask {
    @NotNull File resourceFile(@NotNull String name) {
        return project.file("${Constants.Dirs.MAIN_RESOURCES}/${name}")
    }

    @NotNull File javaFile(@NotNull String name) {
        return project.file("${Constants.Dirs.MAIN_JAVA}/${name}")
    }

    @NotNull File protoFile(@NotNull String name) {
        final var customProtoFile = project.rootProject.file("${Constants.Dirs.LOCAL_PROTO}/${name}")

        if (customProtoFile.exists())
            return customProtoFile

        return project.rootProject.file("${Constants.Dirs.PROTO}/${name}")
    }

    @NotNull File resourceProtoFile(@NotNull String name) {
        return protoFile("resources/${name}")
    }

    @NotNull File javaProtoFile(@NotNull String name) {
        return protoFile("java/${name}")
    }

    @Internal
    @NotNull Directory getGeneratedResourcesDir() {
        return project.layout.buildDirectory.dir(Constants.Dirs.GENERATED_RESOURCES).get()
    }

    @Internal
    @NotNull Directory getGeneratedJavaDir() {
        return project.layout.buildDirectory.dir(Constants.Dirs.GENERATED_JAVA).get()
    }

    @NotNull File resourceGeneratedFile(@NotNull String name) {
        return project.file("${generatedResourcesDir}/${name}")
    }

    @NotNull File javaGeneratedFile(@NotNull String name) {
        return project.file("${generatedJavaDir}/${name}")
    }

    @Nullable Object requiredParam(@NotNull String name) {
        final var prop = project.findProperty(name)

        if (prop == null)
            err "the parameter `${name}` could not be found. It may not have been specified in a `gradle.properties` file"

        return prop
    }

    @NotNull HashMap<String, Object> createBindings(@NotNull Map<String, Object> paramsWithDefaults) {
        final var bindings = new HashMap<String, Object>()

        for (var paramEntry: paramsWithDefaults.entrySet()) {
            bindings.put(
                    paramEntry.key,
                    project.findProperty(paramEntry.key) ?: paramEntry.value
            )
        }

        return bindings
    }

    void logGeneratedFile(@NotNull File file) {
        log "file \"${file.absolutePath}\" was generated (${project.name} project)"
    }
}
