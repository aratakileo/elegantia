package io.github.aratakileo.elegantia

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.annotations.NotNull

class ElegantiaPlugin implements Plugin<Project> {
    @Override
    void apply(@NotNull Project project) {
        project.extensions.create("elegantia", Utils, project)

        new ElegantiaTasks(project).init()
        new SubprojectTasks(project).init()
    }
}
