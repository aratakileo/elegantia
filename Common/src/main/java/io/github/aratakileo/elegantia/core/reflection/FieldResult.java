package io.github.aratakileo.elegantia.core.reflection;

import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

@ApiStatus.Experimental
public final class FieldResult extends Result<FieldContainer> {
    FieldResult(@NonNull FieldContainer ok) {
        super(ok);
    }

    FieldResult(@NotNull Throwable err) {
        super(err);
    }

    public @NotNull Result<Object> readFromInstance(@NotNull Object instance) {
        return flatMap(field -> field.readFromInstance(instance));
    }

    public @NotNull Result<Object> staticRead() {
        return flatMap(FieldContainer::staticRead);
    }

    public @NotNull ValueResult containedReadFromInstance(@NotNull Object instance) {
        return flatMap(field -> field.containedReadFromInstance(instance))
                .match(ValueResult::new, ValueResult::new);
    }

    public @NotNull ValueResult containedStaticRead() {
        return flatMap(FieldContainer::containedStaticRead)
                .match(ValueResult::new, ValueResult::new);
    }
}
