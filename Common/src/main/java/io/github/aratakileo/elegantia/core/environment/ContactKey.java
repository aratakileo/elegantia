package io.github.aratakileo.elegantia.core.environment;

import org.jetbrains.annotations.NotNull;

public enum ContactKey {
    HOMEPAGE,
    SOURCES,
    ISSUES,
    EMAIL,
    DISCORD;

    private final String lowerName;

    ContactKey() {
        this.lowerName = name().toLowerCase();
    }

    public @NotNull String lowerName() {
        return lowerName;
    }
}
