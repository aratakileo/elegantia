package io.github.aratakileo.elegantia.util;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class Namespace {
    public final static Namespace MINECRAFT = new Namespace("minecraft"),
            ELEGANTIA = new Namespace("elegantia");

    private final String namespace;

    private Namespace(@NotNull String namespace) {
        this.namespace = namespace;
    }

    public @NotNull String get() {
        return namespace;
    }

    public @NotNull Logger getLogger() {
        return LoggerFactory.getLogger(namespace);
    }

    public @NotNull Optional<ModInfo> getModInfo() {
        return ModInfo.get(this);
    }

    public @NotNull ResourceLocation getLocation(@NotNull String path) {
        return new ResourceLocation(namespace, path);
    }

    public boolean equals(@NotNull Namespace namespace) {
        return this.namespace.equals(namespace.namespace);
    }

    public boolean equals(@NotNull String namespace) {
        return this.namespace.equals(namespace);
    }

    @Override
    public @NotNull String toString() {
        return "%s:".formatted(namespace);
    }

    public static @NotNull Namespace of(@NotNull String namespace) {
        namespace = namespace.toLowerCase();

        if (namespace.matches("[a-z0-9_.-]+"))
            return nonCheckedOf(namespace);

        throw new InvalidNamespaceException("Non [a-z0-9_.-]+ characters in namespace `%s`".formatted(namespace));
    }

    public static @NotNull Namespace of(@NotNull ResourceLocation location) {
        return nonCheckedOf(location.getNamespace());
    }

    private static @NotNull Namespace nonCheckedOf(@NotNull String namespace) {
        switch (namespace) {
            case "minecraft" -> {
                return MINECRAFT;
            }

            case "elegantia" -> {
                return ELEGANTIA;
            }
        }

        return new Namespace(namespace);
    }

    public static class InvalidNamespaceException extends RuntimeException {
        public InvalidNamespaceException(@NotNull String msg) {
            super(msg);
        }
    }
}
