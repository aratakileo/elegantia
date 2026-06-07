package io.github.aratakileo.elegantia.common.config;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class ConfigString extends ConfigValue<String, ConfigString> {
    private int minLength = 0, maxLength = Integer.MAX_VALUE;

    ConfigString(@NotNull Supplier<String> defaultValueFactory) {
        super(defaultValueFactory);
    }

    public int minLength() {
        return minLength;
    }

    public @NotNull ConfigString minLength(int value) {
        minLength = Math.max(value, 0);
        return this;
    }

    public int maxLength() {
        return maxLength;
    }

    public @NotNull ConfigString maxLength(int value) {
        maxLength = Math.max(value, 1);
        return this;
    }

    public @NotNull ConfigString filter(@NotNull Pattern pattern) {
        return super.filter(value -> pattern.matcher(value).matches());
    }

    @Override
    protected boolean filter(@NotNull String value) {
        return super.filter(value) && value.length() >= minLength && value.length() <= maxLength;
    }
}
