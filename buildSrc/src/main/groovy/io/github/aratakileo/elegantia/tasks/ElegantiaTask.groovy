package io.github.aratakileo.elegantia.tasks

import org.gradle.api.DefaultTask
import org.jetbrains.annotations.NotNull

import javax.inject.Inject

abstract class ElegantiaTask extends DefaultTask {
    protected final static String LOGGER_PREFIX = "[Elegantia Gradle]"

    @Inject
    ElegantiaTask() {
        group = "elegantia"
    }

    void log(@NotNull Object obj) {
        logger.lifecycle("${LOGGER_PREFIX} ${obj}")
    }

    void err(@NotNull Object err) {
        logger.error("${LOGGER_PREFIX} ${err}")
    }

    void warn(@NotNull Object warn) {
        logger.warn("${LOGGER_PREFIX} ${warn}")
    }
}
