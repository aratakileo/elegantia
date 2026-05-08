package io.github.aratakileo.elegantia.mixin;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Style;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.client.gui.Font$StringRenderOutput")
public class MixinFontRenderOutput {
    @Shadow
    float x;
    @Shadow
    float y;
    @Shadow
    private Matrix4f pose;

    @Shadow
    @Final
    private int packedLightCoords;

    @Shadow
    @Final
    private boolean dropShadow;
    @Shadow
    @Final
    private float dimFactor;
    @Shadow
    @Final
    private float r;
    @Shadow
    @Final
    private float g;
    @Shadow
    @Final
    private float b;
    @Shadow
    @Final
    private float a;

    @Shadow
    @Final
    private Font.DisplayMode mode;

    @Shadow
    @Final
    MultiBufferSource bufferSource;

    @Inject(method = "accept(ILnet/minecraft/network/chat/Style;I)Z", at = @At("HEAD"), cancellable = true)
    private void InlineRenderDrawerAccept(int index, Style style, int codepoint, CallbackInfoReturnable<Boolean> cir) {

    }
}
