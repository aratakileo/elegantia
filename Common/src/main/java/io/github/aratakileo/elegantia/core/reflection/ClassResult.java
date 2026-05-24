package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@ApiStatus.Experimental
public final class ClassResult extends Result<ClassContainer> {
    ClassResult(@NonNull ClassContainer ok) {
        super(ok);
    }

    ClassResult(@NotNull Throwable err) {
        super(err);
    }

    public @NotNull MethodResult method(@NotNull String name, @NotNull Class<?> @NotNull... argTypes) {
        return flatMap(clazz -> clazz.method(name, argTypes)).match(MethodResult::new, MethodResult::new);
    }

    public @NotNull FieldResult field(@NotNull String name) {
        return flatMap(clazz -> clazz.field(name)).match(FieldResult::new, FieldResult::new);
    }
}
