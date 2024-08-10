package io.github.aratakileo.elegantia.client.gui.screen;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.elegantia.world.slot.ElegantedSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    private final static ArrayList<Component> tooltipsForRender = new ArrayList<>();

    public @Nullable TextureDrawable backgroundPanel;

    public AbstractContainerScreen(
            @NotNull T abstractContainerMenu,
            @NotNull Inventory inventory,
            @NotNull Component component
    ) {
        super(abstractContainerMenu, inventory, component);
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

        if (backgroundPanel != null)
            backgroundPanel.render(RectDrawer.of(guiGraphics, getPanelPos(), imageWidth, imageHeight));

        renderBackgroundContent(guiGraphics, mousePos, dt);

        for (final var slot: menu.slots)
            if (slot instanceof ElegantedSlot elegantedSlot)
                elegantedSlot.getIconForRender().ifPresent(icon -> icon.render(RectDrawer.square(
                        guiGraphics,
                        getPanelPos().add(elegantedSlot.getPos()),
                        16
                )));
    }

    @Override
    @Deprecated
    protected final void renderBg(@NotNull GuiGraphics guiGraphics, float dt, int mouseX, int mouseY) {}

    public abstract void renderBackgroundContent(
            @NotNull ElGuiGraphics guiGraphics,
            @NotNull Vector2ic mousePos,
            float dt
    );

    /**
     * @deprecated use {@link #render(ElGuiGraphics, Vector2ic, float)} instead
     */
    @Deprecated
    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        render(ElGuiGraphics.of(guiGraphics), new Vector2ic(mouseX, mouseY), dt);
    }

    public void render(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        super.render(guiGraphics, mousePos.x, mousePos.y, dt);
        renderForeground(guiGraphics, mousePos, dt);
    }

    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        renderTooltip(guiGraphics, mousePos);

        for (final var tooltipMessage: tooltipsForRender)
            guiGraphics.renderTooltip(tooltipMessage, mousePos);

        tooltipsForRender.clear();
    }

    public void showTooltip(@NotNull Component message) {
        tooltipsForRender.add(message);
    }

    public @NotNull Vector2ic getPanelPos() {
        return new Vector2ic((width - imageWidth) / 2, (height - imageHeight) / 2);
    }

    /**
     * @deprecated use {@link #renderTooltip(ElGuiGraphics, Vector2ic)} instead
     */
    @Deprecated
    @Override
    protected final void renderTooltip(@NotNull GuiGraphics guiGraphics, int mousseX, int mouseY) {
        renderTooltip(ElGuiGraphics.of(guiGraphics), new Vector2ic(mousseX, mouseY));
    }

    protected void renderTooltip(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos) {
        super.renderTooltip(guiGraphics, mousePos.x, mousePos.y);
    }
}
