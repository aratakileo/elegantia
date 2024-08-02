package io.github.aratakileo.elegantia.core;

import org.jetbrains.annotations.NotNull;

public class NoSuchModException extends RuntimeException {
    public NoSuchModException(@NotNull String message) {
        super(message);
    }
}
