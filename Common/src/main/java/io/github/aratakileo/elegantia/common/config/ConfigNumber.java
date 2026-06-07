package io.github.aratakileo.elegantia.common.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.function.Supplier;

public final class ConfigNumber<T extends Number> extends ConfigValue<T, ConfigNumber<T>> {
    private T min = null, max = null;

    ConfigNumber(@NotNull Supplier<T> defaultValueFactory) {
        super(defaultValueFactory);
    }

    public @Nullable T min() {
        return min;
    }

    public @NotNull ConfigNumber<T> min(@NotNull T value) {
        min = value;
        return this;
    }

    public @Nullable T max() {
        return max;
    }

    public @NotNull ConfigNumber<T> max(@NotNull T value) {
        max = value;
        return this;
    }

    @Override
    protected boolean filter(@NotNull T value) {
        if (min == null && max == null) return super.filter(value);

        var result = super.filter(value);

        final var normalizedValue = new BigDecimal(value.toString());

        if (min != null) {
            final var minComparison = normalizedValue.compareTo(new BigDecimal(min.toString()));
            result = result && minComparison >= 0;
        }

        if (max != null) {
            final var maxComparison = normalizedValue.compareTo(new BigDecimal(max.toString()));
            result = result && maxComparison <= 0;
        }

        return result;
    }

    @Override
    protected boolean load(@NotNull Object value) {
        synchronized (this) {
            if (value instanceof Number number) {
                final var targetClass = unwrapDefault().getClass();
                var convertedValue = number;

                if (targetClass == Integer.class) convertedValue = number.intValue();
                else if (targetClass == Long.class) convertedValue = number.longValue();
                else if (targetClass == Short.class) convertedValue = number.shortValue();
                else if (targetClass == Float.class) convertedValue = number.floatValue();
                else if (targetClass == Double.class) convertedValue = number.doubleValue();

                return super.load(convertedValue);
            }

            return super.load(value);
        }
    }
}
