package io.github.aratakileo.elegantia.client.gui.screen;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.gui.widget.AbstractWidget;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractScreen extends Screen {
    protected final @Nullable Screen parent;

    protected AbstractScreen(@Nullable Screen parent, @NotNull Component component) {
        super(component);
        this.parent = parent;
    }

    /**
     * Use {@link #render(ElGuiGraphics, Vector2ic, float)} instead
     */
    @Deprecated
    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        render(ElGuiGraphics.of(guiGraphics), new Vector2ic(mouseX, mouseY), dt);
    }

    /**
     * @deprecated use {@link #renderBackground(ElGuiGraphics, Vector2ic, float)} instead
     */
    @Deprecated
    @Override
    public final void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        renderBackground(ElGuiGraphics.of(guiGraphics), new Vector2ic(mouseX, mouseY), dt);
    }

    public void renderBackground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        super.renderBackground(guiGraphics, mousePos.x, mousePos.y, dt);
    }

    public void render(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        renderBackground(guiGraphics, mousePos, dt);

        for (final var renderable: renderables) {
            if (renderable instanceof AbstractWidget elWidget) {
                elWidget.render(guiGraphics, mousePos, dt);
                continue;
            }

            renderable.render(guiGraphics, mousePos.x, mousePos.y, dt);
        }

        renderForeground(guiGraphics, mousePos, dt);
    }

    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {}

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }
}
