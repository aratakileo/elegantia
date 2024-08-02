package io.github.aratakileo.elegantia.config;


import org.jetbrains.annotations.NotNull;

public class InvalidTriggerException extends RuntimeException {
    public InvalidTriggerException(@NotNull String message) {
        super(message);
    }
}
