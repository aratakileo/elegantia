package io.github.aratakileo.elegantia.util;

import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public interface HudRenderCallback {
    Event<HudRenderCallback> EVENT = Event.newEventFactory(listeners -> (guiGraphics, dt) -> listeners.forEach(
            event -> event.onHudRender(guiGraphics, dt)
    ));

    void onHudRender(@NotNull GuiGraphics guiGraphics, float dt);
}
