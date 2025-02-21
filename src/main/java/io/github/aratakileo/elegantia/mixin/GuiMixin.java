package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.client.event.HudRenderListener;
import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import net.minecraft.client.DeltaTracker;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void render(
            @NotNull GuiGraphics guiGraphics,
            @NotNull DeltaTracker deltaTracker,
            @NotNull CallbackInfo callbackInfo
    ) {
        HudRenderListener.EVENT.getInvoker().onHudRender(ElGuiGraphics.of(guiGraphics));

    }
}
