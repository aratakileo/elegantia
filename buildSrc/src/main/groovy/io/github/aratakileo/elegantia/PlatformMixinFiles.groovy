package io.github.aratakileo.elegantia

import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.jetbrains.annotations.NotNull

import java.util.regex.Pattern

final class PlatformMixinFiles {
    public final static Pattern PATTERN = ~/(?i).*\.mixins?\.json$/

    private final Project project

    PlatformMixinFiles(@NotNull Project project) {
        this.project = project
    }

    @NotNull List<String> process() {
        final var commonResources = project.rootProject.project(":Common").file(Constants.Dirs.MAIN_RESOURCES)
        final var subprojectResources = project.file(Constants.Dirs.MAIN_RESOURCES)
        final var refmapFileName = "${project.findProperty("mod_id")}-${project.name.toLowerCase()}-refmap.json"

        configureMixinPlugin(project, refmapFileName)
        configureMixinPlugin(project, "${project.findProperty("mod_id")}-common-refmap.json")

        final var subprojectGeneratedResources = project.layout.buildDirectory.file(Constants.Dirs.GENERATED_RESOURCES)
                .get().asFile

        final var subprojectGeneratedMixins = new File("${subprojectGeneratedResources}/mixins")

        final var mixinPaths = [] as List<String>

        if (commonResources.exists())
            commonResources.eachFileMatch(FileType.FILES, PATTERN)  {
                mixinPaths.add(Utils.normalizeSlashes(commonResources.toPath().relativize(it.toPath())))
            }

        if (subprojectResources.exists()) {
            if (!subprojectGeneratedMixins.exists())
                subprojectGeneratedMixins.mkdirs()

            subprojectResources.eachFileMatch(FileType.FILES, PATTERN) { srcFile ->
                final var newFile = new File(
                        "${subprojectGeneratedMixins}/${project.name.toLowerCase()}.${srcFile.name}"
                )

                final var srcData = new JsonSlurper().parse(srcFile) as Map

                srcData.put("refmap", refmapFileName)

                newFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(srcData))

                mixinPaths.add(Utils.normalizeSlashes(subprojectGeneratedResources.toPath().relativize(newFile.toPath())))
            }
        }

        return mixinPaths
    }

    static void configureMixinPlugin(Project project, String refmapName) {
        // Находим расширение loom как generic Object, без каста к классам Loom
        final var loomExtension = project.extensions.findByName("loom")
        if (loomExtension == null) {
            project.logger.warn("[Elegantia] Loom plugin not found in project :${project.name}, skipping mixin configuration.")
            return
        }

        try {
            if (loomExtension instanceof org.gradle.api.plugins.ExtensionAware) {
                // Достаем mixin-расширение по строковому имени
                final var mixinExtension = loomExtension.extensions.findByName("mixin")

                if (mixinExtension != null) {
                    final var mainSourceSet = project.sourceSets.main

                    // Вызываем метод add() динамически. Groovy сам найдет нужный метод под капотом!
                    mixinExtension.add(mainSourceSet, refmapName)
                    project.logger.lifecycle("[Elegantia] Successfully registered refmap '${refmapName}' for :${project.name}")
                }
            }
        } catch (Exception e) {
            project.logger.error("[Elegantia] Failed to configure mixin via reflection/dynamic call", e)
        }

        // Принудительно заставляем процессор аннотаций компилятора Java не падать,
        // если маппинги для Common еще не успели синхронизироваться.
        project.tasks.withType(org.gradle.api.tasks.compile.JavaCompile).configureEach {
            options.compilerArgs << "-Asilent=true"
        }
    }

}
