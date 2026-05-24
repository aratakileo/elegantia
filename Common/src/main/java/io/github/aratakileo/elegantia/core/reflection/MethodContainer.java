package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;

@ApiStatus.Experimental
public final class MethodContainer {
    private final Method method;

    public MethodContainer(@NotNull Method method) {
        this.method = method;
    }

    public @NotNull Result<Object> staticCall(@Nullable Object @NotNull... args) {
        return Result.fromFactory(() -> method.invoke(null, args));
    }

    public @NotNull Result<Object> instanceCall(@NotNull Object instance, @Nullable Object @NotNull... args) {
        return Result.fromFactory(() -> method.invoke(instance, args));
    }

    public @NotNull ValueResult containedStaticCall(@Nullable Object @NotNull... args) {
        return Result.fromFactory(() -> method.invoke(null, args))
                .map(ValueContainer::new)
                .match(ValueResult::new, ValueResult::new);
    }

    public @NotNull ValueResult containedInstanceCall(
            @NotNull Object instance,
            @Nullable Object @NotNull... args
    ) {
        return Result.fromFactory(() -> method.invoke(instance, args))
                .map(ValueContainer::new)
                .match(ValueResult::new, ValueResult::new);
    }
}
