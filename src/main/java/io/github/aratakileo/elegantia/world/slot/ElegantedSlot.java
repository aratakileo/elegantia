package io.github.aratakileo.elegantia.world.slot;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.Vector2iInterface;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElegantedSlot extends Slot {
    public final @Nullable SlotController controller;
    private @Nullable InitOnGet<TextureDrawable> icon;

    public boolean renderBackground = true;

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

    public @NotNull ElegantedSlot setRenderBackground(boolean renderBackground) {
        this.renderBackground = renderBackground;
        return this;
    }

    /**
     * The function allows safely set icon outside of render thread and avoid game crash
     * because of {@link TextureDrawer#getTextureSize(ResourceLocation)}
     */
    public @NotNull ElegantedSlot setIcon(@Nullable InitOnGet<TextureDrawable> icon) {
        this.icon = icon;

        return this;
    }

    /**
     * The function allows safely set icon outside of render thread and avoid game crash
     * because of {@link TextureDrawer#getTextureSize(ResourceLocation)}
     */
    public @NotNull ElegantedSlot setIcon(@Nullable ResourceLocation icon) {
        if (icon == null) {
            this.icon = null;
            return this;
        }

        this.icon = TextureDrawable.safeAutoSize(icon);
        return this;
    }

    public @NotNull ElegantedSlot setIcon(@Nullable TextureDrawable icon) {
        if (icon == null) {
            this.icon = null;
            return this;
        }

        this.icon = InitOnGet.of(icon);
        return this;
    }

    public @NotNull Optional<TextureDrawable> getIcon() {
        if (icon == null)
            return Optional.empty();

        return Optional.of(icon.get());
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
