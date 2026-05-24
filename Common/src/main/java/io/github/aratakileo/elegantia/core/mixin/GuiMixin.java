package io.github.aratakileo.elegantia.core.mixin;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.client.event.HudRenderer;
import io.github.aratakileo.elegantia.client.graphics.ElegantiaGui;
import net.minecraft.client.DeltaTracker;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class GuiMixin {
    @Inject(method = "render", at = @At("TAIL"))
    public void elegantia$renderAtTail(
            @NotNull GuiGraphics guiGraphics,
            @NotNull DeltaTracker deltaTracker,
            @NotNull CallbackInfo callbackInfo
    ) {
        if (Loader.current() == Loader.FORGE) return;
        HudRenderer.RENDER.invoker().onHudRender(ElegantiaGui.from(guiGraphics));
    }

    @Inject(method = "renderEffects", at = @At("HEAD"))
    public void elegantia$renderEffects(
            @NotNull GuiGraphics guiGraphics,
            @NotNull DeltaTracker deltaTracker,
            @NotNull CallbackInfo callbackInfo
    ) {
        // WHAT THE FLUFF IS WRONG WITH FORGE??? Why does it change the vanilla sources that much? This is awful ;(

        if (Loader.current() != Loader.FORGE) return;
        HudRenderer.RENDER.invoker().onHudRender(ElegantiaGui.from(guiGraphics));
    }
}
