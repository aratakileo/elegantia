package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.gui.screen.ConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        Minecraft.getInstance().setScreen(ConfigScreen.of(Elegantia.ElegantiaConfig.class, (TitleScreen)(Object)this));
    }
}
