package io.github.aratakileo.elegantia

import groovy.io.FileType
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import io.github.aratakileo.elegantia.tasks.FabricBasedGenTask
import io.github.aratakileo.elegantia.tasks.ForgeBasedGenTask
import io.github.aratakileo.elegantia.tasks.GenTask
import io.github.aratakileo.elegantia.util.Configurations
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.TaskProvider
import org.jetbrains.annotations.NotNull
import org.gradle.api.file.DuplicatesStrategy
import org.jetbrains.annotations.Nullable
import org.objectweb.asm.*

class SubprojectTasks {
    private final Project project

    SubprojectTasks(@NotNull Project project) {
        this.project = project
    }

    void init() {
        initDataTasks()

        project.afterEvaluate {
            project.findProject(":Common")?.with {commonProj ->
                Configurations.includeMainGeneratedResources(commonProj, true, false)
                initCommonTasks(commonProj)
            }

            project.findProject(":Fabric")?.with {fabricProj ->
                Configurations.includeMainGeneratedResources(fabricProj)
                initFabricTasks(fabricProj)
            }

            project.findProject(":Forge")?.with {forgeProj ->
                Configurations.includeMainGeneratedResources(forgeProj)
                initForgeTasks(forgeProj)
            }

            project.findProject(":NeoForge")?.with {forgeProj ->
                Configurations.includeMainGeneratedResources(forgeProj)
                initNeoForgeTasks(forgeProj)
            }
        }
    }

    private void initDataTasks() {
        final var buildDir = project.layout.buildDirectory.get()

        project.tasks.register("prepareBindingsDataFromFabric") {
            group = "elegantia"

            final var inputFile = project.project(":Fabric").file(Constants.Files.ORIGINAL_FABRIC_MANIFEST)
            final var outputFile = project.file("${buildDir}/${Constants.Files.MANIFEST_BINDINGS}")

            inputs.file(inputFile)
            outputs.file(outputFile)

            doLast {
                outputFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(getFabricBindingsFromManifest(inputFile) ?: Map.of()))
            }
        }

        project.tasks.register("prepareEntryPointsData") {
            group = "elegantia"

            dependsOn(":Common:classes")

            final var outputFile = project.file("${buildDir}/${Constants.Files.ENTRY_POINTS}")

            outputs.file(outputFile)

            doLast {
                final var entries = findEntryPointsInBytecode()

                if (entries == null) {
                    outputFile.text = "{}"
                    return
                }

                final var targetClassPath = entries.common ?: entries.client

                if (!targetClassPath) {
                    project.elegantia.err "mod entry points have classes but all of them have no any entry methods"
                    return
                }

                final var fullClassName = targetClassPath.substring(0, targetClassPath.lastIndexOf('.'))
                final var mod_id = project.elegantia.findPropWithNotifOnFail("mod_id")

                if (!mod_id) return

                final var rawPackageName = fullClassName.contains('.') ?
                        fullClassName.substring(0, fullClassName.lastIndexOf('.')) :
                        "dev.eleganted.mod.${mod_id.replace("-", "_")}.generated"

                final var result = [
                        entries: entries,
                        platform_package: rawPackageName,
                        platform_path: rawPackageName.replace(".", "/")
                ]

                outputFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(result))
            }
        }
    }

    private void initFabricTasks(@NotNull Project fabricProj) {
        final var generateFabricAssets = fabricProj.tasks.register("generateFabricAssets", FabricBasedGenTask) {
            it.entryPointsFile.set(
                    getTaskCachedFile("prepareEntryPointsData")
            )
        }

        final var commonProj = fabricProj.project(":Common")
        final var generateAccessFiles = commonProj.tasks.named("generateAccessFiles")

        final var accessFile = commonProj.layout.buildDirectory.file(
                "${Constants.Dirs.GENERATED_RESOURCES}/${fabricProj.findProperty("mod_id")}.accesswidener"
        ).get()

        if (accessFile.asFile.exists())
            Configurations.loomAccessWidener(
                    fabricProj,
                    accessFile
            )

        Configurations.resourceProcessing(fabricProj, { task ->
            task.from generateAccessFiles, generateFabricAssets

            task.exclude Constants.Files.ORIGINAL_FABRIC_MANIFEST
            task.from generateFabricAssets
            task.dependsOn generateFabricAssets
        })

        configureGenerationTask(fabricProj, generateFabricAssets)
    }

    private void initForgeTasks(@NotNull Project forgeProj) {
        final var generateForgeAssets = forgeProj.tasks.register("generateForgeAssets", ForgeBasedGenTask) {
            it.bindingsFromFabricFile.set(getTaskCachedFile("prepareBindingsDataFromFabric"))
            it.entryPointsFile.set(getTaskCachedFile("prepareEntryPointsData"))
        }

        configureGenerationTask(forgeProj, generateForgeAssets)
    }

    private @NotNull Provider<RegularFile> getTaskCachedFile(@NotNull String taskName) {
        return project.rootProject.tasks.named(taskName)
                .flatMap {task ->
                    task.outputs.files.elements.map {it.first() as RegularFile}
                }
    }

    private void initNeoForgeTasks(@NotNull Project neoForgeProj) {
        final var generateForgeAssets = neoForgeProj.tasks.register("generateNeoForgeAssets", ForgeBasedGenTask) {
            it.bindingsFromFabricFile.set(getTaskCachedFile("prepareBindingsDataFromFabric"))
            it.entryPointsFile.set(getTaskCachedFile("prepareEntryPointsData"))
        }

        configureGenerationTask(neoForgeProj, generateForgeAssets)
    }

    private void initCommonTasks(@NotNull Project commonProj) {
        final var generateAccessFiles = commonProj.tasks.register("generateAccessFiles", GenTask) {
            it.inputs.files commonProj.fileTree(Constants.Dirs.MAIN_RESOURCES).include("**/*.accesswidener")
            it.outputs.dir commonProj.layout.buildDirectory.dir(Constants.Dirs.GENERATED_RESOURCES)

            it.doLast { new AccessFileGenerator(commonProj, [commonProj], true, false).generate() }
        }

        Configurations.resourceProcessing(commonProj, { it.from(generateAccessFiles) })
        Configurations.neoForgeAccessTransformer(commonProj, generateAccessFiles)

        final var generateCommonAssets = commonProj.tasks.register("generateCommonAssets", GenTask) {
            final var fileName = "pack.mcmeta"
            final var protoFile = it.resourceProtoFile(fileName)
            final var genFile = it.resourceGeneratedFile(fileName)

            if (!genFile.parentFile.exists()) genFile.parentFile.mkdirs()

            final var bindings = it.createBindings(
                    mod_name: "eleganted mod",
                    pack_format: 75
            )

            genFile.text = Utils.formatWithBindings(protoFile, bindings)

            final var mainResources = commonProj.file(Constants.Dirs.MAIN_RESOURCES)
            final var generatedResources = commonProj.layout.buildDirectory.file(Constants.Dirs.GENERATED_RESOURCES)
                    .get().asFile

            commonProj.file(mainResources).eachFileMatch(FileType.FILES, PlatformMixinFiles.PATTERN)  {srcFile ->
                final var fileRelativePath = Utils.normalizeSlashes(mainResources.toPath().relativize(srcFile.toPath()))
                final var newFile = new File("${generatedResources}/${fileRelativePath}")
                final var srcData = new JsonSlurper().parse(srcFile) as Map
//                final var refmapFileName = "${project.findProperty("mod_id")}-common-refmap.json"

//                PlatformMixinFiles.configureMixinPlugin(commonProj, refmapFileName)

//                srcData.put("refmap", refmapFileName)

                newFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(srcData))
            }

            it.logGeneratedFile(genFile)
        }

        configureGenerationTask(commonProj, generateCommonAssets)
    }

    private static void configureGenerationTask(@NotNull Project target, @NotNull TaskProvider<Task> task) {
        Configurations.resourceProcessing(target, {
            it.duplicatesStrategy = DuplicatesStrategy.INCLUDE
            it.dependsOn(task)

            it.exclude "${Constants.Dirs.MAIN_RESOURCES}/*.mixins.json", "${Constants.Dirs.MAIN_RESOURCES}/**/*.mixins.json"
        })

        Configurations.javaCompilation(target, { it.dependsOn(task) })
    }

    private @Nullable Map<String, Object> getFabricBindingsFromManifest(@NotNull File inputFile) {
        if (!inputFile.exists()) return null

        final var properties = new HashMap(project.extensions.extraProperties.getProperties())

        if (project.findProperty("version"))
            properties.put("version", project.findProperty("version"))

        final var finalizedJsonText = Utils.formatWithBindings(inputFile, properties)
        final var srcData = new JsonSlurper().parseText(finalizedJsonText) as Map<String, Object>
        final var resultData = new HashMap()

        final var hasLibBadge = srcData.custom?.modmenu?.badges?.contains("library")

        if (srcData.containsKey("id"))
            resultData.put("mod_id", srcData.id)

        if (srcData.containsKey("version"))
            resultData.put("version", srcData.version)

        if (srcData.containsKey("name")) {
            var name = srcData.name as String

            if (hasLibBadge)
                name += " (Library)"

            resultData.put("mod_name", name)
        }

        if (srcData.containsKey("description"))
            resultData.put("description", srcData.description)

        if (srcData.containsKey("authors") && !(srcData.authors as List).isEmpty()) {
            final var authors = srcData.authors as List

            final var authorsString =
                    JsonOutput.toJson(authors[0] instanceof Map
                            ? (authors as List<Map>).collect {it.name.toString()}
                            : authors)

            resultData.put("authors", authorsString)
        }

        if (srcData.containsKey("contact")) {
            final var contacts = srcData.contact as Map

            if (contacts.containsKey("homepage"))
                resultData.put("homepage", contacts.homepage)

            if (contacts.containsKey("issues"))
                resultData.put("issues", contacts.issues)
        }

        if (srcData.containsKey("license"))
            resultData.put("license", srcData.license)

        if (srcData.containsKey("icon"))
            resultData.put("icon", srcData.icon)

        if (srcData.containsKey("environment"))
            resultData.put("client_side", srcData.environment == "client")

        if (srcData.containsKey("depends")) {
            final var depends = srcData.depends as Map
            var dependsString = ""

            for (var dependEntry: depends.entrySet()) {
                if (dependEntry.key as String in ["fabric", "fabric-api", "fabricloader"]) continue

                var version = dependEntry.value as String

                if (version.startsWith(">="))
                    version = "[${version.substring(2,)},)"
                else if (version.startsWith(">"))
                    version = "(${version.substring(1,)},)"
                else if (version.startsWith("<="))
                    version = "(,${version.substring(2,)}]"
                else if (version.startsWith("<"))
                    version = "(,${version.substring(1,)})"
                else if (version.startsWith("!="))
                    version = "!${version.substring(2,)}"
                else if (version.startsWith("="))
                    version = "[${version.substring(1,)}]"

                dependsString += """
                [[dependencies.${resultData.mod_id}]]
                modId="${dependEntry.key}"
                mandatory=true
                versionRange="${version}"
                ordering="NONE"
                side="BOTH"
                """.stripIndent()
            }

            if (!dependsString.isEmpty())
                resultData.put("dependencies", dependsString)
        }

        return resultData
    }

    private @Nullable Map<String, String> findEntryPointsInBytecode() {
        def result = [common: null, client: null]
        def commonProject = project.findProject(":Common")

        if (!commonProject) {
            project.elegantia.err "project :Common not found"
            return null
        }

        def classesDir = commonProject.layout.buildDirectory.dir(Constants.Dirs.CLASSES).get().asFile

        if (!classesDir.exists()) {
            project.elegantia.warn "classes directory not found. Make sure :Common:classes has run"
            return null
        }

        classesDir.eachFileRecurse { file ->
            if (file.name.endsWith(".class") && !file.name.contains('$')) {
                file.withInputStream { is ->
                    def reader = new ClassReader(is)
                    def currentClass = reader.className.replace('/', '.')

                    boolean hasModEntry = false
                    boolean isClientOnly = false

                    reader.accept(new ClassVisitor(Opcodes.ASM9) {
                        @Override
                        AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor == Constants.Bytecodes.MOD_ENTRY_DESCRIPTOR) {
                                hasModEntry = true
                                return new AnnotationVisitor(Opcodes.ASM9) {
                                    @Override
                                    void visitEnum(String name, String desc, String value) {
                                        if (value == "CLIENT") isClientOnly = true
                                    }
                                }
                            }

                            return null
                        }

                        @Override
                        MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                            if (!hasModEntry) return null

                            return new MethodVisitor(Opcodes.ASM9) {
                                @Override
                                AnnotationVisitor visitAnnotation(String d, boolean visible) {
                                    if (d == Constants.Bytecodes.ENTRY_INITIALIZER_DESCRIPTOR) {
                                        def fullPath = "${currentClass}.${name}"

                                        if (isClientOnly) result.client = fullPath
                                        else result.common = fullPath
                                    }

                                    return null
                                }
                            }
                        }
                    }, ClassReader.SKIP_CODE | ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES)
                }
            }
        }

        return result
    }
}
