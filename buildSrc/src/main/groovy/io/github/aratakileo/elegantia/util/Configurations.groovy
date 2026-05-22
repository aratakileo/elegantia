package io.github.aratakileo.elegantia.util

import io.github.aratakileo.elegantia.Constants
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.annotations.NotNull

final class Configurations {
    private Configurations() {}

    static void resourceProcessing(@NotNull Project project, @NotNull Action<ProcessResources> action) {
        project.plugins.withId("java") {
            project.tasks.named("processResources", ProcessResources).configure(action)
        }
    }

    static void javaCompilation(@NotNull Project project, @NotNull Action<JavaCompile> action) {
        project.plugins.withId("java") {
            project.tasks.named("compileJava", JavaCompile).configure(action)
        }
    }

    static void sourceSets(@NotNull Project project, @NotNull String name, @NotNull Action<SourceSet> action) {
        project.plugins.withId("java") {
            action(project.getExtensions().getByType(SourceSetContainer.class).getByName(name))
        }
    }

    static void loomAccessWidener(@NotNull Project project, @NotNull RegularFile file) {
        fabricLoom(project, { it.accessWidenerPath = file })
    }

    static void fabricLoom(@NotNull Project project, @NotNull Action action) {
        project.plugins.withId("fabric-loom") {
            project.configure(project) {
                loom(action)
            }
        }
    }

    static void neoForgeAccessTransformer(@NotNull Project project, @NotNull TaskProvider<Task> provider) {
        neoForge(project, {
            if (project.hasProperty("neo_form_version"))
                it.neoFormVersion = project.findProperty("neo_form_version")

            it.accessTransformers.from(provider.map { task ->
                new File(task.outputs.files.asPath, "META-INF/accesstransformer.cfg")
            })

            if (project.hasProperty('parchment_minecraft')) {
                it.parchment {
                    minecraftVersion = project.parchment_minecraft
                    mappingsVersion = project.parchment_version
                }
            }
        })
    }

    static void neoForge(@NotNull Project project, @NotNull Action action) {
        project.plugins.withId("net.neoforged.moddev") {
            project.extensions.configure("neoForge", action)
        }
    }

    static void includeMainGeneratedResources(
            @NotNull Project project,
            boolean java = true,
            boolean resources = true
    ) {
        includeGeneratedResources(project, "main", java, resources)
    }

    static void includeGeneratedResources(
            @NotNull Project project,
            @NotNull String name,
            boolean java = true,
            boolean resources = true
    ) {
        sourceSets(project, name, {mainSourceSet ->
            if (java) mainSourceSet.getJava().srcDir(project.layout.buildDirectory.dir(Constants.Dirs.GENERATED_JAVA))
            if (resources)
                mainSourceSet.getResources().srcDir(project.layout.buildDirectory.dir(Constants.Dirs.GENERATED_RESOURCES))
        })
    }
}
