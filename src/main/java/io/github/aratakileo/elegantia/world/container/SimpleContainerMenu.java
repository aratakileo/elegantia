package io.github.aratakileo.elegantia.world.container;

import io.github.aratakileo.elegantia.world.container.ContainerAutoData;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class SimpleContainerMenu<T extends ContainerAutoData> extends AbstractContainerMenu {
    protected final T data;
    protected final Container container;

    protected SimpleContainerMenu(
            @Nullable MenuType<?> menuType,
            @NotNull Container container,
            @NotNull T data,
            int syncId
    ) {
        super(menuType, syncId);
        this.container = container;
        this.data = data;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        var newStack = ItemStack.EMPTY;
        final var slot = slots.get(slotIndex);

        if (slot.hasItem()) {
            final var itemStack = slot.getItem();
            newStack = itemStack.copy();

            if (slotIndex < container.getContainerSize()) {
                if (!moveItemStackTo(itemStack, this.container.getContainerSize(), this.slots.size(), true))
                    return ItemStack.EMPTY;
            } else if (!moveItemStackTo(newStack, 0, this.container.getContainerSize(), false))
                return ItemStack.EMPTY;

            if (itemStack.isEmpty()) slot.setByPlayer(ItemStack.EMPTY);
            else slot.setChanged();
        }

        return newStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return container.stillValid(player);
    }

    protected void addPlayerInventorySlots(@NotNull Inventory inventory) {
        for (var y = 0; y < 3; ++y)
            for (var x = 0; x < 9; ++x)
                addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
    }

    protected void addPlayerHotbarSlots(@NotNull Inventory inventory) {
        for (var i = 0; i < 9; ++i)
            addSlot(new Slot(inventory, i, 8 + i * 18, 142));
    }
}
