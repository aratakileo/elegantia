package io.github.aratakileo.elegantia.common.config;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public final class ConfigEnum<T extends Enum<T>> extends ConfigValue<T, ConfigEnum<T>> {
    private JsonView view = null;

    ConfigEnum(@NotNull Supplier<T> defaultValueFactory) {
        super(defaultValueFactory);
    }

    public @NotNull ConfigEnum<T> jsonView(@NotNull JsonView view) {
        if (this.view != null) throw new IllegalStateException();

        this.view = view;
        return this;
    }

    @Override
    protected boolean load(@NotNull Object value) {
        synchronized (this) {
            final var allConstants = unwrapDefault().getClass().getEnumConstants();

            final var newValue = switch (value) {
                case String str -> Arrays.stream(allConstants)
                        .filter(_value -> _value.name().equalsIgnoreCase(str))
                        .findFirst()
                        .orElseThrow();

                case Integer _int -> allConstants[_int];
                case Enum<?> _enum -> _enum;

                default -> throw new IllegalStateException("unexpected value: " + value);
            };

            return super.load(newValue);
        }
    }

    @Override
    public @NotNull Object unwrapJsonable() {
        return view == JsonView.NUMBER ? unwrap().ordinal() : unwrap().name();
    }

    public enum JsonView {
        NAME,
        NUMBER
    }
}
