package io.github.aratakileo.elegantia.client.event;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

public interface HudRenderListener {
    Event<HudRenderListener> EVENT = Event.newEventFactory(listeners -> guiGraphics -> listeners.forEach(
            hudRenderListener -> {
                if (hudRenderListener.shouldDraw())
                    hudRenderListener.onHudRender(guiGraphics);
            }
    ));

    void onHudRender(@NotNull ElGuiGraphics guiGraphics);

    default boolean shouldDraw() {
        return !Minecraft.getInstance().options.hideGui && !Minecraft.getInstance().getDebugOverlay().showDebugScreen();
    }
}
