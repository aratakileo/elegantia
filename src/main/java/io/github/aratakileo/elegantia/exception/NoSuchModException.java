package io.github.aratakileo.elegantia.exception;

import org.jetbrains.annotations.NotNull;

public class NoSuchModException extends RuntimeException {
    public NoSuchModException(@NotNull String message) {
        super(message);
    }
}
