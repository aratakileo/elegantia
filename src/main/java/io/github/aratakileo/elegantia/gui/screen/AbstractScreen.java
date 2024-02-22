package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.graphics.GuiGraphicsUtil;
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
        renderBackground(guiGraphics, mouseX, mouseY, dt);
        super.render(guiGraphics, mouseX, mouseY, dt);
        renderContent(guiGraphics, mouseX, mouseY, dt);
    }

// 1.20-1.20.1 only
    @Override
    @Deprecated(since = "0.0.1-beta", forRemoval = true)
    public final void renderBackground(GuiGraphics guiGraphics) {
        throw new RuntimeException("The use of this variant of the method is prohibited!");
    }

    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
// 1.20-1.20.1
        super.renderBackground(guiGraphics);
// 1.20.2-1.20.4
//        super.renderBackground(guiGraphics, mouseX, mouseY, dt);
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

    public static void setScreen(@NotNull Screen screen) {
        Minecraft.getInstance().setScreen(screen);
    }
}
