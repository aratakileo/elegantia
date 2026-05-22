package io.github.aratakileo.elegantia.tasks

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.github.aratakileo.elegantia.PlatformMixinFiles
import io.github.aratakileo.elegantia.Utils
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.jetbrains.annotations.NotNull

abstract class ForgeBasedGenTask extends GenTask {
    static final LinkedHashMap<String, ?> MANIFEST_PROPS_WITH_DEFAULTS = [
            mod_name: "eleganted mod",
            version: "1.0.0",
            license: "All Rights Reserved",
            forge_loader: "*",
            neoforge_loader: "*",
            issues: null,
            show_resource_pack: false,
            client_side: false,
            homepage: null,
            icon: null,
            authors: null,
            description: null
    ]

    @Internal
    boolean isForge() {
        return platformName == "Forge"
    }

    @Internal
    @NotNull String getPlatformId() {
        return platformName.toLowerCase()
    }

    @Internal
    @NotNull String getPlatformName() {
        return project.name
    }

    @Internal
    @NotNull String getManifestFileName() {
        return "META-INF/${isForge() ? "mods.toml" : "neoforge.mods.toml"}"
    }

    @Internal
    @NotNull String getEntryFileName() {
        return "${platformId}/${platformName}Entry.java"
    }

    @InputFile
    abstract RegularFileProperty getBindingsFromFabricFile()

    @InputFile
    abstract RegularFileProperty getEntryPointsFile()

    private void generateManifest(@NotNull Object mod_id) {
        final var manifestProtoFile = resourceProtoFile(manifestFileName)
        final var manifestGenFile = resourceGeneratedFile(manifestFileName)

        if (!manifestGenFile.parentFile.exists()) manifestGenFile.parentFile.mkdirs()

        final var bindings = createBindings(MANIFEST_PROPS_WITH_DEFAULTS)
        final var bindingsFromFabric = new JsonSlurper().parse(bindingsFromFabricFile.get().asFile) as Map

        if (!bindingsFromFabric.isEmpty()) bindings.putAll(bindingsFromFabric)

        final var mixinPaths = new PlatformMixinFiles(project).process()

        bindings.put("mod_id", mod_id)
        bindings.put("forge_mixins", JsonOutput.toJson(mixinPaths))
        bindings.put("neoforge_mixins", mixinPaths.collect {"[[mixins]]\nconfig=\"${it}\""}.join("\n\n"))

        manifestGenFile.text = formatManifestAlikeFile(manifestProtoFile, bindings)

        logGeneratedFile(manifestGenFile)
    }

    private void generateJava(@NotNull Object mod_id) {
        final var entriesData = new JsonSlurper().parse(entryPointsFile.get().asFile) as Map

        if (entriesData.isEmpty()) return

        final var entryProtoFile = javaProtoFile(entryFileName)
        final var entryGenFile = javaGeneratedFile("${entriesData.platform_path}/${entryFileName}")

        if (!entryGenFile.parentFile.exists()) entryGenFile.parentFile.mkdirs()

        final var protoBindings = [
                package_name: "${entriesData.platform_package}.${platformId}",
                mod_id: mod_id,
                mod_common_entry: entriesData.entries.common
                        ? "${entriesData.entries.common}();"
                        : "// No common entries",

                mod_client_entry: entriesData.entries.client
                        ? "${entriesData.entries.client}();"
                        : "{ /* No client entries */ }"
        ]

        entryGenFile.text = Utils.formatWithBindings(entryProtoFile, protoBindings)

        logGeneratedFile(entryGenFile)
    }

    @TaskAction
    void generate() {
        final var mod_id = requiredParam("mod_id")

        if (mod_id == null) return

        generateManifest(mod_id)
        generateJava(mod_id)
    }

    private static @NotNull String formatManifestAlikeFile(@NotNull File file, @NotNull Map<String, Object> bindings) {
        var multiStringInProcess = false;

        return file.readLines().collect {line ->
            if (line.strip().isEmpty()) return line

            if (line.stripLeading().startsWith("?{") && !multiStringInProcess) {
                final var starts = line.indexOf("?")
                final var ends = line.indexOf("}")
                final var target = line.substring(starts + 2, ends).strip()

                if (!bindings.containsKey(target) || bindings.get(target) == null) return ""

                line = line.substring(ends + 1)
            }

            final var threeQuotes = line.count("'''")

            if (threeQuotes % 2 == 1) multiStringInProcess = !multiStringInProcess

            return Utils.formatWithBindings(line, bindings)
        }.join("\n")
    }
}
