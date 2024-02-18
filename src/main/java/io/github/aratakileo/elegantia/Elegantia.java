package io.github.aratakileo.elegantia;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigEntry;
import io.github.aratakileo.elegantia.util.HudRenderCallback;
import io.github.aratakileo.elegantia.util.graphics.RectDrawer;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Elegantia implements ClientModInitializer {
    public final static Logger LOGGER = LoggerFactory.getLogger(Elegantia.class);
    public final static String MODID = "elegantia";

    public class Conf extends Config {
        @ConfigEntry
        public boolean choZaUrodi = false;
    }

    @Override
    public void onInitializeClient() {
        Config.init(Conf.class, "elegantia");
        HudRenderCallback.EVENT.register((guiGraphics, dt) -> new RectDrawer(
                guiGraphics,
                50,
                50,
                50
        ).drawGradient(0xff4caf50, 0xfff44336, RectDrawer.GradientDirection.CORNER_LEFT_TOP));
        HudRenderCallback.EVENT.register((guiGraphics, dt) -> new RectDrawer(
                guiGraphics,
                150,
                50,
                50
        ).drawGradient(0xff4caf50, 0xfff44336, RectDrawer.GradientDirection.CORNER_RIGHT_TOP));
        HudRenderCallback.EVENT.register((guiGraphics, dt) -> new RectDrawer(
                guiGraphics,
                50,
                150,
                50
        ).drawGradient(0xff4caf50, 0xfff44336, RectDrawer.GradientDirection.CORNER_LEFT_BOTTOM));
        HudRenderCallback.EVENT.register((guiGraphics, dt) -> new RectDrawer(
                guiGraphics,
                150,
                150,
                50
        ).drawGradient(0xff4caf50, 0xfff44336, RectDrawer.GradientDirection.CORNER_RIGHT_BOTTOM));
    }
}
