package io.github.aratakileo.elegantia.client.config;


import org.jetbrains.annotations.NotNull;

public class InvalidTriggerException extends RuntimeException {
    public InvalidTriggerException(@NotNull String message) {
        super(message);
    }
}
