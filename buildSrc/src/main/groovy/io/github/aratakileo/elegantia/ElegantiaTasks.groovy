package io.github.aratakileo.elegantia

import io.github.aratakileo.elegantia.tasks.ElegantiaTask
import org.gradle.api.GradleException
import org.gradle.api.Project
import org.jetbrains.annotations.NotNull
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.JavaExec
import org.gradle.api.file.DuplicatesStrategy

class ElegantiaTasks {
      private final Project project

      ElegantiaTasks(@NotNull Project project) {
            this.project = project
          }

      void init() {
            registerUniversalTasks()
            registerRunTasks()
          }

      private void registerUniversalTasks() {
            def commonProject = project.findProject(":Common") ?: project.findProject(":common")

            def universalBinaryJar = project.tasks.register('universalBinaryJar', Jar) {
                  group = "elegantia"
                  archiveBaseName = "${project.mod_id}-${project.version}+${project.minecraft_version}"
                  destinationDirectory = project.rootProject.layout.buildDirectory.dir(Constants.Dirs.LIBS)
                  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                  Constants.SUBPROJECTS.each { name ->
                        def sub = project.findProject(":${name}")
                        if (sub && name.toLowerCase() != "fabric") {
                              from sub.sourceSets.main.output
                              dependsOn sub.tasks.named("classes")
                            }
                      }

                  doFirst {
                        project.elegantia.log "binary file: ${archiveFile.get().asFile.absolutePath}"
                      }

                  manifest {
                        attributes([
                                    "Specification-Title"  : project.mod_name,
                                    "Specification-Vendor"  : "Arataki Leo",
                                    "Specification-Version" : "1",
                                    "Implementation-Title"  : project.mod_name,
                                    "Implementation-Version" : project.version,
                                    "Implementation-Vendor" : "Arataki Leo",
                                    "MixinConfigs"      : "${project.mod_id}.mixins.json"
                                ])
                      }
                }

            def universalSourcesJar = project.tasks.register('universalSourcesJar', Jar) {
                  group = "elegantia"
                  archiveBaseName = "${project.mod_id}-${project.version}+${project.minecraft_version}"
                  archiveClassifier = 'sources'
                  destinationDirectory = project.rootProject.layout.buildDirectory.dir(Constants.Dirs.LIBS)
                  duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                  Constants.SUBPROJECTS.each { name ->
                        def sub = project.findProject(":${name}")

                        if (sub) {
                              from sub.sourceSets.main.allSource
                            }
                      }
                }

            Constants.SUBPROJECTS.each { name ->
                  def sub = project.findProject(":${name}")
                project.tasks.register("${sub.name.toLowerCase()}ThinJar", Jar) {
                    group = "elegantia"
                    archiveBaseName = "${project.mod_id}-${sub.name.toLowerCase()}"
                    archiveVersion = "${project.version}+${project.minecraft_version}"
                    destinationDirectory = project.rootProject.layout.buildDirectory.dir(Constants.Dirs.LIBS)
                    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                    if (sub.name.toLowerCase() == "fabric") {
                        def remapJarTask = sub.tasks.named("remapJar")
                        dependsOn remapJarTask

                        from { project.zipTree(remapJarTask.get().archiveFile) }
                    } else {
                        from sub.sourceSets.main.output
                        dependsOn sub.tasks.named("classes")
                    }

                    if (commonProject) {
                        from commonProject.sourceSets.main.output
                        dependsOn commonProject.tasks.named("sourcesJar")
                    }

                    doFirst {
                        project.elegantia.log "Building THIN binary file for ${sub.name}: ${archiveFile.get().asFile.absolutePath}"
                    }

                    manifest {
                        attributes([
                                "Specification-Title"  : "${project.mod_name} (${sub.name})",
                                "Specification-Vendor"  : "Arataki Leo",
                                "Specification-Version" : "1",
                                "Implementation-Title"  : "${project.mod_name} (${sub.name})",
                                "Implementation-Version" : project.version,
                                "Implementation-Vendor" : "Arataki Leo",
                                "MixinConfigs"      : "${project.mod_id}.mixins.json"
                        ])
                    }
                }

                  project.tasks.register("${sub.name.toLowerCase()}ThinSourcesJar", Jar) {
                        group = "elegantia"
                        archiveBaseName = "${project.mod_id}-${sub.name.toLowerCase()}"
                        archiveVersion = "${project.version}+${project.minecraft_version}"
                        archiveClassifier = 'sources'
                        destinationDirectory = project.rootProject.layout.buildDirectory.dir(Constants.Dirs.LIBS)
                        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

                        from sub.sourceSets.main.allSource

                        if (commonProject) {
                              from commonProject.sourceSets.main.allSource
                            }

                        doFirst {
                              project.elegantia.log "Building THIN sources file for ${sub.name}: ${archiveFile.get().asFile.absolutePath}"
                            }
                      }
                }

            project.tasks.register("buildUniversals", ElegantiaTask) {
                  dependsOn universalBinaryJar, universalSourcesJar

                  Constants.SUBPROJECTS.each { name ->
                        dependsOn "${name.toLowerCase()}ThinJar"
                        dependsOn "${name.toLowerCase()}ThinSourcesJar"
                      }
                }

            project.tasks.register("configurePrototypes", ElegantiaTask) {
                  final var configurableDir = project.file("${project.rootDir}/${Constants.Dirs.LOCAL_PROTO}")
                  final var protosDir = project.file(Constants.Dirs.PROTO)

                  if (!configurableDir.exists())
                    configurableDir.mkdirs()
                  else project.elegantia.log "it looks like some configurable files already exist. This task will not override them. To override them just remove files you want to override and then run this task again"

                  project.copy {
                        from protosDir.absolutePath
                        into configurableDir.absolutePath

                        eachFile { details ->
                              if (new File(configurableDir.absolutePath, details.path).exists())
                                details.exclude()
                            }

                        includeEmptyDirs = false
                      }

                  project.elegantia.log "prototype files now available: ${configurableDir.absolutePath}"
                }
          }

      private void registerRunTasks() {
            def universalJarTask = project.tasks.named('universalBinaryJar', Jar)

            project.tasks.register("runFabricClient", ElegantiaTask) {
                  dependsOn "buildUniversals"

                  doLast {
                        def fabricProject = project.findProject(":Fabric") ?: project.findProject(":fabric")
                        if (fabricProject) {
                              def fabricRun = fabricProject.tasks.named("runClient", JavaExec.class).get()
                              def jarFile = universalJarTask.get().archiveFile.get().asFile

                              def targetWorkingDir = fabricRun.workingDir
                              if (targetWorkingDir == fabricProject.projectDir) {
                                    targetWorkingDir = fabricProject.file("runs/client")
                                  }

                              if (!targetWorkingDir.exists()) {
                                    targetWorkingDir.mkdirs()
                                  }
                              fabricRun.workingDir = targetWorkingDir

                              fabricRun.classpath = fabricRun.classpath.filter { file ->
                                    !file.path.contains("build/classes") &&
                                        !file.path.contains("build/resources")
                                  }.plus(project.files(jarFile))

                              fabricRun.jvmArgs("-Dfabric.addMod=${jarFile.absolutePath}")

                              project.elegantia.log "launching Fabric with working directory: ${targetWorkingDir.absolutePath}"
                              project.elegantia.log "launching Fabric with injected JAR: ${jarFile.name}"

                              fabricRun.exec()
                            }
                      }
                }

            project.tasks.register("runForgeClient", ElegantiaTask) {
                  dependsOn "buildUniversals"

                  doFirst {
                        project.elegantia.log "preparing Forge execution environment..."
                      }

                  doLast {
                        def forgeProject = project.findProject(":Forge")
                        if (forgeProject) {
                              def forgeRunTask = forgeProject.tasks.named("runClient", JavaExec).get()
                              def jarFile = universalJarTask.get().archiveFile.get().asFile

                              forgeRunTask.classpath += project.files(jarFile)
                              forgeRunTask.jvmArgs(
                                          "-Dfml.ignoreInvalidMinecraftCertificates=true",
                                          "-Dforge.enabledGameTestNamespaces=${project.mod_id}",
                                          "-Dfml.modMods=${project.mod_id}%%${jarFile.absolutePath}"
                                      )

                              project.elegantia.log "launching Forge client with JAR: ${jarFile.absolutePath}"
                              forgeRunTask.exec()
                            }
                      }
                }

            project.tasks.register("runNeoForgeClient", ElegantiaTask) {
                  dependsOn "buildUniversals"

                  doFirst {
                        project.elegantia.log "preparing NeoForge execution environment..."
                      }

                  doLast {
                        def neoProject = project.findProject(":NeoForge") ?: project.findProject(":neoforge")

                        if (neoProject) {
                              def neoRunTask = neoProject.tasks.named("runClient", JavaExec).get()
                              def jarFile = universalJarTask.get().archiveFile.get().asFile

                              if (neoRunTask.environment.containsKey("MOD_CLASSES")) {
                                    neoRunTask.environment.remove("MOD_CLASSES")
                                  }

                              neoRunTask.environment("MOD_CLASSES", "${project.mod_id}%%${jarFile.absolutePath}")

                              neoRunTask.classpath = neoRunTask.classpath.filter { file ->
                                    !file.path.contains("build/classes") &&
                                        !file.path.contains("build/resources") &&
                                        !file.path.contains("out/production")
                                  }

                              neoRunTask.jvmArgs(
                                          "-Dneo.subsystems.modFolder=${jarFile.parentFile.absolutePath}",
                                          "-Dneoforge.forceModFile=${jarFile.absolutePath}"
                                      )

                              project.elegantia.log "launching NeoForge client with JAR: ${jarFile.absolutePath}"

                              neoRunTask.exec()
                            } else {
                              throw new GradleException("NeoForge subproject not found!")
                            }
                      }
                }
          }
}