package io.github.aratakileo.elegantia.common.config;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class ConfigValue<T, C extends ConfigValue<T, C>> {
    private final Supplier<T> defaultValueFactory;

    private volatile Function<T, Boolean> filter = null;
    private volatile T value = null;
    private volatile String name = null;
    private volatile boolean loaded = false, dirty = false;

    protected ConfigValue(@NotNull Supplier<T> defaultValueFactory) {
        this.defaultValueFactory = Objects.requireNonNull(defaultValueFactory);
    }

    public boolean loaded() {
        return loaded;
    }

    public @NotNull T unwrap() {
        if (!loaded) throw new IllegalStateException("config value is not loaded yet!");
        return value == null ? defaultValueFactory.get() : value;
    }

    public @NotNull Object unwrapJsonable() {
        return unwrap();
    }

    public @NotNull T unwrapDefault() {
        return defaultValueFactory.get();
    }

    public boolean hasName() {
        return name != null;
    }

    public @NotNull String unwrapName() {
        final var currentName = name;
        if (currentName == null) throw new IllegalStateException("name is not set!");
        return currentName;
    }

    public boolean set(@NotNull T value) {
        synchronized (this) {
            if (this.dirty)
                throw new IllegalStateException("try to set a new value but the old one have not been saved");

            if (!filter(value)) return false;

            this.dirty = true;
            this.value = value;

            return true;
        }
    }

    @SuppressWarnings("unchecked")
    public @NotNull C name(@NotNull String name) {
        synchronized (this) {
            if (this.name != null) throw new IllegalStateException("name is already set!");
            this.name = Objects.requireNonNull(name);
            return (C) this;
        }
    }

    @SuppressWarnings("unchecked")
    public @NotNull C filter(@NotNull Function<T, Boolean> filter) {
        synchronized (this) {
            this.filter = filter;
            return (C) this;
        }
    }

    @ApiStatus.Internal
    @SuppressWarnings("unchecked")
    protected boolean load(@NotNull Object value) {
        synchronized (this) {
            if (this.loaded) throw new IllegalStateException("value is already loaded!");

            if (!filter((T) value)) return false;

            this.value = (T)value;
            this.loaded = true;

            return true;
        }
    }

    final void markAsNonDirty() {
        synchronized (this) {
            dirty = false;
        }
    }

    protected boolean filter(@NotNull T value) {
        return filter == null || filter.apply(value);
    }

    public static @NotNull ConfigBoolean bool(@NotNull Supplier<Boolean> defaultValueFactory) {
        return new ConfigBoolean(defaultValueFactory);
    }

    public static @NotNull ConfigString string(@NotNull Supplier<String> defaultValueFactory) {
        return new ConfigString(defaultValueFactory);
    }

    public static @NotNull ConfigNumber<Short> shortInteger(@NotNull Supplier<Short> defaultValueFactory) {
        return new ConfigNumber<>(defaultValueFactory);
    }

    public static @NotNull ConfigNumber<Integer> integer(@NotNull Supplier<Integer> defaultValueFactory) {
        return new ConfigNumber<>(defaultValueFactory);
    }

    public static @NotNull ConfigNumber<Long> longInteger(@NotNull Supplier<Long> defaultValueFactory) {
        return new ConfigNumber<>(defaultValueFactory);
    }

    public static @NotNull ConfigNumber<Float> floatDecimal(@NotNull Supplier<Float> defaultValueFactory) {
        return new ConfigNumber<>(defaultValueFactory);
    }

    public static @NotNull ConfigNumber<Double> doubleDecimal(@NotNull Supplier<Double> defaultValueFactory) {
        return new ConfigNumber<>(defaultValueFactory);
    }

    public static <T extends Enum<T>> @NotNull ConfigEnum<T> enumerable(@NotNull Supplier<T> defaultValueFactory) {
        return new ConfigEnum<>(defaultValueFactory);
    }
}
