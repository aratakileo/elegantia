package io.github.aratakileo.elegantia.event;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Event<T> {
    protected volatile T invoker;
    
    private final List<T> handlers = new ArrayList<>();
    private final Function<List<T>, T> invokerFactory;

    private Event(Function<List<T>, T> invokerFactory) {
        this.invokerFactory = invokerFactory;
    }

    public T getInvoker() {
        return invoker;
    }
    
    public void register(T listener) {
        synchronized (handlers) {
            handlers.add(listener);
            update();
        }
    }

    private void update() {
        invoker = invokerFactory.apply(handlers);
    }

    public static <T> @NotNull Event<T> newEventFactory(@NotNull Function<List<T>, T> invokerFactory) {
        final var event = new Event<>(invokerFactory);
        event.update();
        return event;
    }
}
