package io.github.aratakileo.elegantia.tasks

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.github.aratakileo.elegantia.AccessFileGenerator
import io.github.aratakileo.elegantia.Constants
import io.github.aratakileo.elegantia.PlatformMixinFiles
import io.github.aratakileo.elegantia.Utils
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.annotations.NotNull

abstract class FabricBasedGenTask extends GenTask {
    public final static String MANIFEST_FILE_NAME = "fabric.mod.json"
    public final static String COMMON_ENTRY_CLASS = "FabricCommonEntry"
    public final static String CLIENT_ENTRY_CLASS = "FabricClientEntry"
    public final static String SERVER_ENTRY_CLASS = "FabricServerEntry"
    public final static String PRELAUNCH_ENTRY_CLASS = "FabricPrelaunchEntry"
    public final static String COMMON_ENTRY_FILE_NAME = "fabric/${COMMON_ENTRY_CLASS}.java"
    public final static String CLIENT_ENTRY_FILE_NAME = "fabric/${CLIENT_ENTRY_CLASS}.java"
    public final static String SERVER_ENTRY_FILE_NAME = "fabric/${SERVER_ENTRY_CLASS}.java"
    public final static String PRELAUNCH_ENTRY_FILE_NAME = "fabric/${PRELAUNCH_ENTRY_CLASS}.java"

    @Internal
    @NotNull File getManifestProtoFile() {
        final var defaultFile = project.file(Constants.Files.ORIGINAL_FABRIC_MANIFEST)

        if (defaultFile.exists()) return defaultFile

        return resourceProtoFile(MANIFEST_FILE_NAME)
    }

    @InputFile
    abstract RegularFileProperty getEntryPointsFile()

    private void generateManifest() {
        final var entriesData = new JsonSlurper().parse(entryPointsFile.get().asFile) as Map

        if (entriesData.isEmpty()) return

        final var manifestGenFile = resourceGeneratedFile(MANIFEST_FILE_NAME)

        if (!manifestGenFile.parentFile.exists()) manifestGenFile.parentFile.mkdirs()

        final var sourceData = new JsonSlurper().parse(manifestProtoFile) as HashMap

        if (!sourceData.containsKey("entrypoints"))
            sourceData.put("entrypoints", new HashMap())

        if (entriesData.entries.common != null)
            sourceData.entrypoints.put(
                    "main",
                    ["${entriesData.platform_package}.fabric.${COMMON_ENTRY_CLASS}"]
            )

        if (entriesData.entries.prelaunch != null)
            sourceData.entrypoints.put(
                    "preLaunch",
                    ["${entriesData.platform_package}.fabric.${PRELAUNCH_ENTRY_CLASS}"]
            )

        if (entriesData.entries.client != null)
            sourceData.entrypoints.put(
                    "client",
                    ["${entriesData.platform_package}.fabric.${CLIENT_ENTRY_CLASS}"]
            )

        if (entriesData.entries.server != null)
            sourceData.entrypoints.put(
                    "server",
                    ["${entriesData.platform_package}.fabric.${SERVER_ENTRY_CLASS}"]
            )

        final var resourcesDir = project.layout.buildDirectory.file(Constants.Dirs.GENERATED_RESOURCES)
                .get()
                .asFile.toPath()

        final var accessWidenerFile = project
                .fileTree(resourcesDir)
                .matching {include "**/*.accesswidener"}
                .first()

        if (accessWidenerFile)
            sourceData.put(
                    "accessWidener",
                    Utils.normalizeSlashes(resourcesDir.relativize(accessWidenerFile.toPath()))
            )

        sourceData.put("mixins", new PlatformMixinFiles(project).process())

        manifestGenFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(sourceData))

        logGeneratedFile(manifestGenFile)
    }

    private void generateJava() {
        final var entriesData = new JsonSlurper().parse(entryPointsFile.get().asFile) as Map

        if (entriesData.isEmpty()) return

        generateEntryJava(entriesData, "common")
        generateEntryJava(entriesData, "client")
        generateEntryJava(entriesData, "server")
        generateEntryJava(entriesData, "prelaunch")
    }

    private void generateEntryJava(@NotNull Map entriesData, @NotNull String entryKey) {
        if (entriesData.entries.get(entryKey) == null) return

        final var fileName = switch (entryKey) {
            case "common" -> COMMON_ENTRY_FILE_NAME;
            case "client" -> CLIENT_ENTRY_FILE_NAME;
            case "prelaunch" -> PRELAUNCH_ENTRY_FILE_NAME;
            default -> SERVER_ENTRY_FILE_NAME;
        }

        final var entryProtoFile = javaProtoFile(fileName)
        final var entryGenFile = javaGeneratedFile("${entriesData.platform_path}/${fileName}")

        if (!entryGenFile.parentFile.exists()) entryGenFile.parentFile.mkdirs()

        final var protoBindings = [
                package_name: "${entriesData.platform_package}.fabric",
                mod_common_entry: entriesData.entries.common
                        ? "${entriesData.entries.common.path}();"
                        : "// No common entries",

                mod_prelaunch_entry: entriesData.entries.prelaunch
                        ? "${entriesData.entries.prelaunch.path}();"
                        : "// No prelaunch entries",

                mod_server_entry: entriesData.entries.server
                        ? "${entriesData.entries.server.path}();"
                        : "// No server entries",

                mod_client_entry: entriesData.entries.client
                        ? "${entriesData.entries.client.path}();"
                        : "// No client entries"
        ]

        entryGenFile.text = Utils.formatWithBindings(entryProtoFile, protoBindings)

        logGeneratedFile(entryGenFile)
    }

    @TaskAction
    void generate() {
        new AccessFileGenerator(
                project,
                [project, project.rootProject.project(":Common")],
                false
        ).generate()

        generateManifest()
        generateJava()
    }
}
