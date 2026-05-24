package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

@ApiStatus.Experimental
public final class FieldContainer {
    private final Field field;

    public FieldContainer(@NotNull Field field) {
        this.field = field;
    }

    public @NotNull Result<Object> readFromInstance(@NotNull Object instance) {
        return Result.fromFactory(() -> field.get(instance));
    }

    public @NotNull Result<Object> staticRead() {
        return Result.fromFactory(() -> field.get(null));
    }

    public @NotNull ValueResult containedReadFromInstance(@NotNull Object instance) {
        return Result.fromFactory(() -> field.get(instance))
                .map(ValueContainer::new)
                .match(ValueResult::new, ValueResult::new);
    }

    public @NotNull ValueResult containedStaticRead() {
        return Result.fromFactory(() -> field.get(null))
                .map(ValueContainer::new)
                .match(ValueResult::new, ValueResult::new);
    }
}
