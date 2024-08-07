package io.github.aratakileo.elegantia.world.slot;

import io.github.aratakileo.elegantia.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.math.Vector2iInterface;
import io.github.aratakileo.elegantia.math.Vector2ic;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class ElegantedSlot extends Slot {
    public final @Nullable SlotController controller;

    private @Nullable TextureDrawable icon = null;
    private @Nullable Supplier<TextureDrawable> iconGetter = null;

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

    /**
     * The function sets the getter that is called on first try to get or render an icon.
     * It must be used to avoid crash if {@link TextureDrawer#getTextureSize(ResourceLocation)}
     * is called when creating the {@link TextureDrawable} instance outside the render thread
     */
    public @NotNull ElegantedSlot setIconGetter(@Nullable Supplier<TextureDrawable> iconGetter) {
        this.icon = null;
        this.iconGetter = iconGetter;

        return this;
    }

    public @NotNull ElegantedSlot setIcon(@Nullable TextureDrawable icon) {
        this.icon = icon;
        this.iconGetter = null;
        return this;
    }

    public @NotNull Optional<TextureDrawable> getIcon() {
        if (icon == null && iconGetter != null)
            return Optional.of(iconGetter.get());

        if (icon != null)
            return Optional.of(icon);

        return Optional.empty();
    }

    public @NotNull Optional<TextureDrawable> getIconForRender() {
        return getIcon().map(icon -> hasItem() ? null : icon);
    }

    public @NotNull Vector2ic getPos() {
        return new Vector2ic(x, y);
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
    public @NotNull ItemStack safeInsert(@NotNull ItemStack itemStack) {
        return controller == null
                ? super.safeInsert(itemStack)
                : controller.safeInsert(getItem(), itemStack, this::setByPlayer);
    }

    @Override
    public @NotNull ItemStack safeInsert(@NotNull ItemStack itemStack, int count) {
        return controller == null
                ? super.safeInsert(itemStack, count)
                : controller.safeInsert(getItem(), itemStack, count, this::setByPlayer);
    }
}
