package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.graphics.GuiGraphicsUtil;
import io.github.aratakileo.elegantia.gui.slot.IconedSlot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public abstract class AbstractContainerScreen<T extends AbstractContainerMenu> extends net.minecraft.client.gui.screens.inventory.AbstractContainerScreen<T> {
    private final static ArrayList<Component> tooltipsForRender = new ArrayList<>();

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
        renderBackgroundContent(guiGraphics, mouseX, mouseY, dt);

        for (final var slot: menu.slots)
            if (slot instanceof IconedSlot iconedSlot)
                iconedSlot.renderIcon(guiGraphics);
    }

    @Override
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
}
