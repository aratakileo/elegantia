package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.gui.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ElegantiaConfig extends Config {
    public static @Nullable ElegantiaConfig instance;

    public static @NotNull ElegantiaConfig getInstance() {
        if (Objects.isNull(instance))
            throw new ElegantiaConfigIsNotInitedException("an attempt to get an instance before initializing it");

        return instance;
    }

    public static class ElegantiaConfigIsNotInitedException extends RuntimeException {
        public ElegantiaConfigIsNotInitedException(@NotNull String message) {
            super(message);
        }
    }
}
