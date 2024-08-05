package io.github.aratakileo.elegantia.world.slot;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public interface SlotController {
    SlotController RESULT = new Builder().prohibitPlacement().build();

    default int getMaxStackSize(@NotNull ItemStack insertable) {
        return insertable.getMaxStackSize();
    }

    default boolean mayPlace(@NotNull ItemStack slot, @NotNull ItemStack insertable) {
        return true;
    }

    default @NotNull ItemStack safeInsert(
            @NotNull ItemStack slot,
            @NotNull ItemStack insertable,
            @NotNull Consumer<ItemStack> slotSetter
    ) {
       return safeInsert(slot, insertable, insertable.getCount(), slotSetter);
    }

    default @NotNull ItemStack safeInsert(
            @NotNull ItemStack slot,
            @NotNull ItemStack insertable,
            int insertableCount,
            @NotNull Consumer<ItemStack> slotSetter
    ) {
        if (insertable.isEmpty() || mayPlace(slot, insertable))
            return insertable;

        insertableCount = Math.min(
                Math.min(insertable.getCount(), insertableCount),
                getMaxStackSize(insertable) - slot.getCount()
        );

        if (slot.isEmpty()) {
            slotSetter.accept(insertable.split(insertableCount));
            return insertable;
        }

        if (ItemStack.isSameItemSameComponents(slot, insertable)) {
            insertable.shrink(insertableCount);
            slot.grow(insertableCount);
            slotSetter.accept(slot);
        }

        return insertable;
    }

    abstract class AbstractBuilder<S extends SlotController, B extends AbstractBuilder<S, B>> {
        protected @Nullable GetMaxStackSize maxStackSizeGetter = null;

        @SuppressWarnings("unchecked")
        public @NotNull B setSingeItemMaxStackSize() {
            this.maxStackSizeGetter = insertable -> 1;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public @NotNull B setMaxStackSizeGetter(@Nullable GetMaxStackSize maxStackSizeGetter) {
            this.maxStackSizeGetter = maxStackSizeGetter;
            return (B) this;
        }

        abstract public @NotNull S build();

        public interface GetMaxStackSize {
            int getMaxStackSize(@NotNull ItemStack insertable);
        }
    }

    class Builder extends AbstractBuilder<SlotController, Builder> {
        protected @Nullable MayPlace mayPlace = null;
        protected @Nullable SafeInsert safeInsert = null;

        public @NotNull Builder prohibitPlacement() {
            this.mayPlace = (slot, insertable) -> false;
            return this;
        }

        public @NotNull Builder setMayPlace(@Nullable MayPlace mayPlace) {
            this.mayPlace = mayPlace;
            return this;
        }

        public @NotNull Builder setSafeInsert(@Nullable SafeInsert safeInsert) {
            this.safeInsert = safeInsert;
            return this;
        }

        public @NotNull SlotController build() {
            return new SlotController() {
                @Override
                public int getMaxStackSize(@NotNull ItemStack insertable) {
                    return maxStackSizeGetter == null
                            ? SlotController.super.getMaxStackSize(insertable)
                            : maxStackSizeGetter.getMaxStackSize(insertable);
                }

                @Override
                public boolean mayPlace(@NotNull ItemStack slot, @NotNull ItemStack insertable) {
                    return mayPlace == null
                            ? SlotController.super.mayPlace(slot, insertable)
                            : mayPlace.mayPlace(slot, insertable);
                }

                @Override
                public @NotNull ItemStack safeInsert(
                        @NotNull ItemStack slot,
                        @NotNull ItemStack insertable,
                        int insertableCount,
                        @NotNull Consumer<ItemStack> slotSetter
                ) {
                    return safeInsert == null
                            ? SlotController.super.safeInsert(slot, insertable, insertableCount, slotSetter)
                            : safeInsert.safeInsert(slot, insertable, insertableCount, slotSetter);
                }
            };
        }

        public interface MayPlace {
            boolean mayPlace(@NotNull ItemStack slot, @NotNull ItemStack insertable);
        }

        public interface SafeInsert {
            @NotNull ItemStack safeInsert(
                    @NotNull ItemStack slot,
                    @NotNull ItemStack insertable,
                    int insertableCount,
                    @NotNull Consumer<ItemStack> slotSetter
            );
        }
    }
}
