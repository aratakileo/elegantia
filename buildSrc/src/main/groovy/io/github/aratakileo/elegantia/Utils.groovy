package io.github.aratakileo.elegantia

import groovy.text.SimpleTemplateEngine
import org.jetbrains.annotations.Nullable
import org.gradle.api.Project
import org.jetbrains.annotations.NotNull

import java.nio.file.Path

class Utils {
    private final Project project
    private final String prefix = "elegantia-gradle> "

    Utils(@NotNull Project project) {
        this.project = project
    }

    void log(@NotNull Object message) {
        project.logger.lifecycle("${prefix}${message}")
    }

    void err(@NotNull Object err) {
        project.logger.error("${prefix}${err}")
    }

    void warn(@NotNull Object warn) {
        project.logger.warn("${prefix}${warn}")
    }

    @Nullable Object findPropWithNotifOnFail(@NotNull String name) {
        final var property = project.findProperty(name)

        if (property == null)
            warn("the \"${name}\" parameter was not found in the \"gradle.properties\" file")

        return property
    }

    static @NotNull String formatWithBindings(@NotNull String text, @NotNull Map<String, Object> bindings) {
        return new SimpleTemplateEngine()
                .createTemplate(text)
                .make(bindings)
                .toString()
    }

    static @NotNull String formatWithBindings(@NotNull File file, @NotNull Map<String, Object> bindings) {
        return new SimpleTemplateEngine()
                .createTemplate(file)
                .make(bindings)
                .toString()
    }

    static @NotNull String normalizeSlashes(@NotNull Path path) {
        return normalizeSlashes(path.toString())
    }

    static @NotNull String normalizeSlashes(@NotNull String path) {
        return path.replace("\\", "/")
    }
}
