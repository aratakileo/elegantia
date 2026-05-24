package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.util.Classes;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Experimental
public final class ClassContainer {
    private final Class<?> clazz;

    private ClassContainer(@NotNull Class<?> clazz) {
        this.clazz = clazz;
    }

    public @NotNull MethodResult method(@NotNull String name, @NotNull Class<?> @NotNull... types) {
        return Result.fromFactory(() -> clazz.getMethod(name, types))
                .map(MethodContainer::new)
                .match(MethodResult::new, MethodResult::new);
    }

    public @NotNull FieldResult field(@NotNull String name) {
        return Result.fromFactory(() -> clazz.getField(name))
                .map(FieldContainer::new)
                .match(FieldResult::new, FieldResult::new);
    }

    public static @NotNull ClassResult load(@NotNull String className) {
        return Classes.load(className).map(ClassContainer::new).match(ClassResult::new, ClassResult::new);
    }

    public static @NotNull ClassContainer create(@NotNull Class<?> clazz) {
        return new ClassContainer(clazz);
    }
}
