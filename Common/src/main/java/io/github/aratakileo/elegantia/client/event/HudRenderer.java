package io.github.aratakileo.elegantia.client.event;

import io.github.aratakileo.elegantia.core.Event;
import io.github.aratakileo.elegantia.client.gui.graphics.ElegantiaGui;
import io.github.aratakileo.elegantia.client.util.GameUtils;
import org.jetbrains.annotations.NotNull;

public final class HudRenderer {
    public static Event<Listener> RENDER;

    @FunctionalInterface
    public interface Listener {
        void onHudRender(@NotNull ElegantiaGui elegantiaGui);

        default boolean shouldBeDrawn() {
            return GameUtils.hudShouldBeDrawn();
        }
    }

    static {
        RENDER = Event.create(Listener.class, listeners -> gui -> {
            for (final var listener: listeners)
                if (listener.shouldBeDrawn()) listener.onHudRender(gui);
        });
    }
}
