package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.util.GuiGraphicsUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScreen extends Screen {
    protected @Nullable Screen parent;

    protected AbstractScreen(@NotNull Component component, @Nullable Screen parent) {
        super(component);
        this.parent = parent;
    }

    protected AbstractScreen(@NotNull Component component) {
        this(component, getCurrentScreen());
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
// 1.20-1.20.1
        renderBackground(guiGraphics);
// 1.20.2-1.20.4
//        renderBackground(guiGraphics, mouseX, mouseY, dt);
        super.render(guiGraphics, mouseX, mouseY, dt);
        renderContent(guiGraphics, mouseX, mouseY, dt);
    }

    public void renderContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        GuiGraphicsUtil.drawCenteredText(guiGraphics, title, width / 2, 15, 0xffffff);
    }

    public int getContentY() {
        return 40;
    }

    public static @Nullable Screen getCurrentScreen() {
        return Minecraft.getInstance().screen;
    }
}
