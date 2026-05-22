package io.github.aratakileo.elegantia.framework.event;

import io.github.aratakileo.elegantia.util.Arrays;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;
import java.util.function.Function;

public class Event<T> {
    protected volatile T invoker;

    private @NotNull T[] handlers;
    private final Function<T[], T> invokerFactory;

    @SuppressWarnings("unchecked")
    protected Event(@NotNull Class<T> type, @NotNull Function<T[], T> invokerFactory) {
        this.invokerFactory = invokerFactory;
        this.handlers = (T[])Array.newInstance(type, 0);
        update();
    }

    public T invoker() {
        return invoker;
    }

    public void register(@NotNull T listener) {
        synchronized (this) {
            handlers = Arrays.expandWith(handlers, listener);
            update();
        }
    }

    protected void update() {
        invoker = invokerFactory.apply(handlers);
    }

    public static <T> @NotNull Event<T> create(@NotNull Class<T> type, @NotNull Function<T[], T> invokerFactory) {
        return new Event<>(type, invokerFactory);
    }
}
