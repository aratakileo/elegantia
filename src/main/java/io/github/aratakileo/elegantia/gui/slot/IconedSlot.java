package io.github.aratakileo.elegantia.gui.slot;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class IconedSlot extends Slot {
    private final TextureDrawable icon;

    public IconedSlot(
            @NotNull Container container,
            @NotNull TextureDrawable icon,
            int index,
            @NotNull Vector2iInterface pos
    ) {
        super(container, index, pos.x(), pos.y());
        this.icon = icon;
    }

    public IconedSlot(@NotNull Container container, @NotNull TextureDrawable icon, int index, int x, int y) {
        super(container, index, x, y);
        this.icon = icon;
    }

    public void renderIcon(@NotNull GuiGraphics guiGraphics) {
        if (hasItem()) return;

        icon.render(RectDrawer.of(guiGraphics, x + 1, y + 1, 16));
    }

    public static @NotNull IconedSlot of(
            @NotNull Container container,
            @NotNull ResourceLocation icon,
            int index,
            @NotNull Vector2iInterface pos
    ) {
        return new IconedSlot(container, TextureDrawable.of(icon), index, pos);
    }

    public static @NotNull IconedSlot of(
            @NotNull Container container,
            @NotNull ResourceLocation icon,
            int index,
            int x,
            int y
    ) {
        return new IconedSlot(container, TextureDrawable.of(icon), index, x, y);
    }
}
