package io.github.aratakileo.elegantia.core.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.helpers.MessageFormatter;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public final class Strings {
    private Strings() {}

    public static @NotNull String jsonify(@Nullable String value) {
        if (value == null) return "null";

        final var stringBuilder = new StringBuilder("\"");

        for (final var ch: value.toCharArray())
            stringBuilder.append(escape(ch));

        return stringBuilder.append('"').toString();
    }

    public static @NotNull String jsonify(char value) {
        return "\"" + escape(value) + "\"";
    }

    public static @NotNull String jsonify(float value) {
        return String.valueOf(value);
    }

    public static @NotNull String jsonify(double value) {
        return String.valueOf(value);
    }

    public static @NotNull String jsonify(int value) {
        return String.valueOf(value);
    }

    public static @NotNull String jsonify(long value) {
        return String.valueOf(value);
    }

    public static @NotNull String jsonify(boolean value) {
        return String.valueOf(value);
    }

    public static @NotNull String jsonify(@Nullable Object value) {
        switch (value) {
            case null -> { return "null";}
            case String str -> { return jsonify(str); }
            case Character ch -> { return jsonify((char) ch); }
            case Integer num -> { return jsonify((int) num); }
            case Long num -> { return jsonify((long) num); }
            case Float num -> { return jsonify((float) num); }
            case Double num -> { return jsonify((double) num); }
            case Boolean bool -> { return jsonify((boolean) bool); }
            case Collection<?> collection -> { return jsonify(collection); }
            default -> {}
        }

        if (value.getClass().isArray()) {
            final var length = Array.getLength(value);
            final var objects = new Object[length];

            for (var i = 0; i < length; i++)
                objects[i] = Array.get(value, i);

            return jsonify(objects);
        }

        return jsonify(value.toString());
    }

    public static @NotNull String jsonify(@Nullable Object @Nullable [] value) {
        if (value == null) return "null";

        return "[" + Arrays.stream(value).map(Strings::jsonify).collect(Collectors.joining(", ")) + "]";
    }

    public static @NotNull String jsonify(@Nullable Collection<?> value) {
        if (value == null) return "null";

        return "[" + value.stream().map(Strings::jsonify).collect(Collectors.joining(", ")) + "]";
    }

    public static @NotNull String escape(char value) {
        return switch (value) {
            case '\n' -> "\\n";
            case '\r' -> "\\r";
            case '\t' -> "\\t";
            case '"'  -> "\\\"";
            case '\\' -> "\\\\";
            default   -> String.valueOf(value);
        };
    }

    public static @NotNull String format(@NotNull String message, @Nullable Object @NotNull... args) {
        if (Objects.requireNonNull(args).length == 0) return message;
        return MessageFormatter.arrayFormat(message, args).getMessage();
    }
}
