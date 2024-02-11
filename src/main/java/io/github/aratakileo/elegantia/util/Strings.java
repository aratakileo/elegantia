package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import java.util.function.Function;

public interface Strings {
    static @NotNull String camelToSnake(@NotNull String value) {
        return value.replaceAll("([A-Z]+)([A-Z][a-z])", "$1_$2")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    static @NotNull String requireReturnNotAsArgument(
            @NotNull String argumentValue,
            @NotNull Function<@NotNull String, @NotNull String> processor,
            @NotNull String elseValue
    ) {
        final var functionReturn = processor.apply(argumentValue);

        return functionReturn.equals(argumentValue) ? elseValue : functionReturn;
    }

    static @NotNull String doesNotMeetCondition(
            @NotNull String ifValue,
            @NotNull Function<@NotNull String, @NotNull Boolean> condition,
            @NotNull String elseValue
    ) {
        return !condition.apply(ifValue) ? ifValue : elseValue;
    }

    static @NotNull String substring(
            @NotNull String source,
            @Range(from = 0, to = Integer.MAX_VALUE) int beginIndex,
            @Range(from = Integer.MIN_VALUE, to = Integer.MAX_VALUE) int endIndex
    ) {
        if (endIndex == beginIndex)
            return "";

        if (endIndex < 0) {
            if (endIndex <= -source.length() + beginIndex) return "";

            return source.substring(beginIndex, source.length() - endIndex);
        }

        return source.substring(beginIndex, endIndex);
    }
}
