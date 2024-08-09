package io.github.aratakileo.elegantia.client.gui.screen;

import io.github.aratakileo.elegantia.client.graphics.GuiGraphicsUtil;
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

    @Override
    public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        super.renderBackground(guiGraphics, mouseX, mouseY, dt);

        if (backgroundPanel != null)
            backgroundPanel.render(RectDrawer.of(guiGraphics, getPanelPos(), imageWidth, imageHeight));

        renderBackgroundContent(guiGraphics, mouseX, mouseY, dt);

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

    public abstract void renderBackgroundContent(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt);

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        super.render(guiGraphics, mouseX, mouseY, dt);
        renderForeground(guiGraphics, mouseX, mouseY, dt);
    }

    public void renderForeground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        renderTooltip(guiGraphics, mouseX, mouseY);

        for (final var tooltipMessage: tooltipsForRender)
            GuiGraphicsUtil.renderTooltip(guiGraphics, tooltipMessage, mouseX, mouseY);

        tooltipsForRender.clear();
    }

    public void showTooltip(@NotNull Component message) {
        tooltipsForRender.add(message);
    }

    public @NotNull Vector2ic getPanelPos() {
        return new Vector2ic((width - imageWidth) / 2, (height - imageHeight) / 2);
    }
}
