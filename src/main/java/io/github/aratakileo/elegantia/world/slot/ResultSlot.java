package io.github.aratakileo.elegantia.world.slot;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ResultSlot extends Slot {
    public ResultSlot(@NotNull Container container, int index, int xPos, int yPos) {
        super(container, index, xPos, yPos);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack itemStack) {
        return false;
    }
}
