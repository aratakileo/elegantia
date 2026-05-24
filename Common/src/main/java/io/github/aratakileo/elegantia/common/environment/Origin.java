package io.github.aratakileo.elegantia.common.environment;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public final class Origin {
    private final static ConcurrentHashMap<String, Origin> CACHED;

    public final static Origin MINECRAFT, ELEGANTIA;

    public final String key;

    private Origin(@NotNull String key) {
        this.key = key;
    }

    public @NotNull Logger logger() {
        return LoggerFactory.getLogger(key);
    }

    public @NotNull Identifier id(@NotNull String path) {
        return Identifier.fromNamespaceAndPath(key, path);
    }

    public @NotNull ModProfile unwrapProfile() {
        return Loader.modOptional(key).orElseThrow();
    }

    public boolean is(@NotNull String namespace) {
        return key.equals(namespace);
    }

    @Override
    public @NotNull String toString() {
        return key;
    }

    public static @NotNull Origin from(@NotNull String key) {
        key = key.toLowerCase();

        if (key.matches("[a-z0-9_.-]+"))
            return nonCheckedOf(key);

        throw new IllegalArgumentException("Non [a-z0-9_.-]+ characters in origin `%s`".formatted(key));
    }

    public static @NotNull Origin from(@NotNull Identifier id) {
        return nonCheckedOf(id.getNamespace());
    }

    private static @NotNull Origin nonCheckedOf(@NotNull String key) {
        switch (key) {
            case "minecraft" -> {
                return MINECRAFT;
            }

            case "elegantia" -> {
                return ELEGANTIA;
            }
        }

        // allows comparison by memory cell id
        return CACHED.computeIfAbsent(key, Origin::new);
    }

    static {
        CACHED = new ConcurrentHashMap<>();
        MINECRAFT = new Origin("minecraft");
        ELEGANTIA = new Origin("elegantia");
    }
}
