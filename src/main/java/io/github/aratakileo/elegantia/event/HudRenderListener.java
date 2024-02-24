package io.github.aratakileo.elegantia.event;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public interface HudRenderListener {
    Event<HudRenderListener> EVENT = Event.newEventFactory(listeners -> (guiGraphics, dt) -> listeners.forEach(
            hudRenderListener -> hudRenderListener.onHudRender(guiGraphics, dt)
    ));

    void onHudRender(@NotNull GuiGraphics guiGraphics, float dt);
}
