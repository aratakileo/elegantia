package io.github.aratakileo.elegantia.gui.slot;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ElegantedSlot extends Slot {
    private final @Nullable SlotController controller;

    private @Nullable TextureDrawable icon;

    public ElegantedSlot(
            @NotNull Container container,
            @Nullable SlotController controller,
            int index,
            @NotNull Vector2iInterface pos
    ) {
        super(container, index, pos.x(), pos.y());
        this.controller = controller;
    }

    public ElegantedSlot(
            @NotNull Container container,
            @Nullable SlotController controller,
            int index,
            int x,
            int y
    ) {
        super(container, index, x, y);
        this.controller = controller;
    }

    public @NotNull ElegantedSlot setIcon(@Nullable ResourceLocation icon) {
        this.icon = icon == null ? null : TextureDrawable.of(icon);
        return this;
    }

    public @NotNull ElegantedSlot setIcon(@Nullable TextureDrawable icon) {
        this.icon = icon;
        return this;
    }

    public void renderIcon(@NotNull GuiGraphics guiGraphics) {
        if (hasItem() || icon == null) return;

        icon.render(RectDrawer.of(guiGraphics, x + 1, y + 1, 16));
    }

    @Override
    public int getMaxStackSize(@NotNull ItemStack itemStack) {
        return controller == null ? super.getMaxStackSize(itemStack) : controller.getMaxStackSize(itemStack);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return controller == null ? super.mayPlace(itemStack) : controller.mayPlace(getItem(), itemStack);
    }

    @Override
    public @NotNull ItemStack safeInsert(@NotNull ItemStack itemStack, int count) {
        return controller == null
                ? super.safeInsert(itemStack, count)
                : controller.safeInsert(getItem(), itemStack, count, this::setByPlayer);
    }
}
