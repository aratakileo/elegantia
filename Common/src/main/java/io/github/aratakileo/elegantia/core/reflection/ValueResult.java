package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@ApiStatus.Experimental
public final class ValueResult extends Result<ValueContainer> {
    ValueResult(@NonNull ValueContainer ok) {
        super(ok);
    }

    ValueResult(@NotNull Throwable err) {
        super(err);
    }

    public @NotNull ValueContainer.MethodCallerResult methodCaller(
            @NotNull String methodName,
            @NotNull Class<?> @NotNull... argTypes
    ) {
        return flatMap(value -> value.methodCaller(methodName, argTypes))
                .match(ValueContainer.MethodCallerResult::new, ValueContainer.MethodCallerResult::new);
    }
}
