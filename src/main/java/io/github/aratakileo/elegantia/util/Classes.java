package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public interface Classes {
    static @NotNull String getFieldView(@NotNull Field field) {
        return getFieldView(field.getDeclaringClass(), field.getName());
    }

    static @NotNull String getFieldView(@NotNull Class<?> owner, @NotNull String fieldName) {
        return owner.getName() + '#' + fieldName;
    }
}
