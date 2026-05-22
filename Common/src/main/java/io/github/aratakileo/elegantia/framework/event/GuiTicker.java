package io.github.aratakileo.elegantia.framework.event;

public final class GuiTicker {
    public static Event<Listener> TICK;

    @FunctionalInterface
    public interface Listener {
        void onTick(long deltaTime);
    }

    static {
        TICK = Event.create(Listener.class, listeners -> dt -> {
            for (final var listener: listeners)
                listener.onTick(dt);
        });
    }
}
