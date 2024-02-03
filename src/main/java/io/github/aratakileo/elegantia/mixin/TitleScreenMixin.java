package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.gui.TooltipPositioner;
import io.github.aratakileo.elegantia.gui.screen.AbstractScreen;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    public void init(CallbackInfo ci) {
        Minecraft.getInstance().setScreen(new AbstractScreen(Component.literal("Pohui")) {
            @Override
            protected void init() {
                final var btn = new Button(new Rect2i(2, 2, 100, 20), Component.literal("Click to close this screen"));
                btn.setTooltip("Button_228");
                btn.setTooltipPositionerGetter(TooltipPositioner.HoveredTooltipPositioner::new);
                btn.setOnClickListener((button, byUser) -> {
                    Elegantia.LOGGER.info("Button clicked! By user: " + byUser);

                    this.onClose();

                    return true;
                });
                addRenderableWidget(btn);
            }
        });
    }
}
