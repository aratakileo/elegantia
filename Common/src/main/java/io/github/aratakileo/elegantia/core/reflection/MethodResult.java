package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

@ApiStatus.Experimental
public final class MethodResult extends Result<MethodContainer> {
    MethodResult(@NonNull MethodContainer ok) {
        super(ok);
    }

    MethodResult(@NotNull Throwable err) {
        super(err);
    }

    public @NotNull Result<Object> staticCall(@Nullable Object @NotNull... args) {
        return flatMap(method -> method.staticCall(args));
    }

    public @NotNull Result<Object> instanceCall(@NotNull Object instance, @Nullable Object @NotNull... args) {
        return flatMap(method -> instanceCall(instance, args));
    }

    public @NotNull ValueResult containedStaticCall(@Nullable Object @NotNull... args) {
        return flatMap(method -> method.containedStaticCall(args))
                .match(ValueResult::new, ValueResult::new);
    }

    public @NotNull ValueResult containedInstanceCall(
            @NotNull Object instance,
            @Nullable Object @NotNull... args
    ) {
        return flatMap(method -> method.containedInstanceCall(instance, args))
                .match(ValueResult::new, ValueResult::new);
    }
}
