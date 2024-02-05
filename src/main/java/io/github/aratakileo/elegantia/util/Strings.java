package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Strings {
    public static @NotNull String camelToSnake(@NotNull String value) {
        return value.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    public static @NotNull String requireReturnNotAsArgument(
            @NotNull String argumentValue,
            @NotNull Function<@NotNull String, @NotNull String> processor,
            @NotNull String elseValue
    ) {
        final var functionReturn = processor.apply(argumentValue);

        return functionReturn.equals(argumentValue) ? elseValue : functionReturn;
    }

    public static @NotNull String doesNotMeetCondition(
            @NotNull String elseValue,
            @NotNull Function<@NotNull String, @NotNull Boolean> condition,
            @NotNull String ifValue
    ) {
        return !condition.apply(elseValue) ? elseValue : ifValue;
    }
}
