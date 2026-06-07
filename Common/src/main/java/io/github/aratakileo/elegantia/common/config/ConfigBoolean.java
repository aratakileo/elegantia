package io.github.aratakileo.elegantia.common.config;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public final class ConfigBoolean extends ConfigValue<Boolean, ConfigBoolean> {
    ConfigBoolean(@NotNull Supplier<Boolean> defaultValueFactory) {
        super(defaultValueFactory);
    }
}
