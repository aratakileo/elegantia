package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.Method;

@ApiStatus.Experimental
public final class ValueContainer {
    private final Object value;

    public ValueContainer(@NotNull Object value) {
        this.value = value;
    }

    public @NotNull MethodCallerResult methodCaller(
            @NotNull String methodName,
            @NotNull Class<?> @NotNull... argTypes
    ) {
        return Result.fromFactory(() -> value.getClass().getMethod(methodName, argTypes))
                .map(MethodCaller::new)
                .match(MethodCallerResult::new, MethodCallerResult::new);
    }

    @ApiStatus.Experimental
    public final class MethodCaller {
        private final MethodContainer container;

        private MethodCaller(@NotNull Method method) {
            this.container = new MethodContainer(method);
        }

        public @NotNull Result<Object> call(@Nullable Object @NotNull... args) {
            return container.instanceCall(ValueContainer.this.value, args);
        }

        public @NotNull ValueResult containedCall(@Nullable Object @NotNull... args) {
            return container.containedInstanceCall(ValueContainer.this.value, args);
        }
    }

    @ApiStatus.Experimental
    public final static class MethodCallerResult extends Result<MethodCaller> {
        MethodCallerResult(ValueContainer.@NotNull MethodCaller ok) {
            super(ok);
        }

        MethodCallerResult(@NotNull Throwable err) {
            super(err);
        }

        public @NotNull Result<Object> call(@Nullable Object @NotNull... args) {
            return flatMap(caller -> caller.call(args));
        }

        public @NotNull ValueResult containedCall(@Nullable Object @NotNull... args) {
            return (ValueResult)flatMap(caller -> caller.containedCall(args));
        }
    }
}
