package io.github.aratakileo.elegantia.event;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public interface HudRenderListener {
    Event<HudRenderListener> EVENT = Event.newEventFactory(listeners -> (guiGraphics, deltaTracker) -> listeners.forEach(
            hudRenderListener -> {
                if (hudRenderListener.shouldDraw())
                    hudRenderListener.onHudRender(guiGraphics, deltaTracker);
            }
    ));

    void onHudRender(@NotNull GuiGraphics guiGraphics, DeltaTracker deltaTracker);

    default boolean shouldDraw() {
        return !Minecraft.getInstance().options.hideGui;
    }
}
