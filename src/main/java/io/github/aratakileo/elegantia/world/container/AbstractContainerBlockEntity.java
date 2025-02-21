package io.github.aratakileo.elegantia.world.container;

import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.util.Strings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.UUID;

public abstract class AbstractContainerBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    protected final Block block;

    protected NonNullList<ItemStack> items;

    protected AbstractContainerBlockEntity(
            @NotNull BlockEntityType<?> blockEntityType,
            @NotNull BlockPos blockPos,
            @NotNull BlockState blockState,
            @NotNull Block block,
            int slots
    ) {
        super(blockEntityType, blockPos, blockState);
        this.block = block;
        this.items = NonNullList.withSize(slots, ItemStack.EMPTY);
    }

    @Override
    protected @NotNull Component getDefaultName() {
        return block.getName();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> newItems) {
        items = newItems;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.saveAdditional(compoundTag, provider);

        for (final var field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CompoundDataField.class))
                saveFieldData(compoundTag, this, field, null);
        }

        ContainerHelper.saveAllItems(compoundTag, items, provider);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag compoundTag, @NotNull HolderLookup.Provider provider) {
        super.loadAdditional(compoundTag, provider);

        ContainerHelper.loadAllItems(compoundTag, items, provider);

        for (final var field: this.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(CompoundDataField.class))
                loadFieldData(compoundTag, this, field, null);
        }
    }

    @Override
    public int getContainerSize() {
        return items.size();
    }

    private void saveFieldData(
            @NotNull CompoundTag compoundTag,
            @NotNull Object instance,
            @NotNull Field field,
            @Nullable String prefix
    ) {
        field.setAccessible(true);
        hasSupportedTypeOrThrow(field);

        final var attributeName = getCompoundAttributeName(field, prefix);

        try {
            if (field.getType() == boolean.class)
                compoundTag.putBoolean(attributeName, field.getBoolean(instance));
            else if (field.getType() == int.class)
                compoundTag.putInt(attributeName, field.getInt(instance));
            else if (field.getType() == short.class)
                compoundTag.putShort(attributeName, field.getShort(instance));
            else if (field.getType() == long.class)
                compoundTag.putLong(attributeName, field.getLong(instance));
            else if (field.getType() == float.class)
                compoundTag.putFloat(attributeName, field.getFloat(instance));
            else if (field.getType() == double.class)
                compoundTag.putDouble(attributeName, field.getDouble(instance));
            else if (field.getType() == String.class)
                compoundTag.putString(attributeName, (String)field.get(instance));
            else if (field.getType() == UUID.class)
                compoundTag.putUUID(attributeName, (UUID)field.get(instance));
            else if (field.getType().isEnum())
                compoundTag.putInt(attributeName, ((Enum<?>)field.get(instance)).ordinal());
            else if (Classes.isArrayField(field, int.class))
                compoundTag.putIntArray(attributeName, (int[])field.get(instance));
            else for (final var subfield: ((ContainerAutoData)field.get(instance)).fields)
                saveFieldData(compoundTag, field.get(instance), subfield, field.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFieldData(
            @NotNull CompoundTag compoundTag,
            @NotNull Object instance,
            @NotNull Field field,
            @Nullable String prefix
    ) {
        field.setAccessible(true);
        hasSupportedTypeOrThrow(field);

        final var attributeName = getCompoundAttributeName(field, prefix);

        try {
            if (field.getType() == boolean.class)
                field.setBoolean(instance, compoundTag.getBoolean(attributeName));
            else if (field.getType() == int.class)
                field.setInt(instance, compoundTag.getInt(attributeName));
            else if (field.getType() == short.class)
                field.setShort(instance, compoundTag.getShort(attributeName));
            else if (field.getType() == long.class)
                field.setLong(instance, compoundTag.getLong(attributeName));
            else if (field.getType() == float.class)
                field.setFloat(instance, compoundTag.getFloat(attributeName));
            else if (field.getType() == double.class)
                field.setDouble(instance, compoundTag.getDouble(attributeName));
            else if (field.getType() == String.class)
                field.set(instance, compoundTag.getString(attributeName));
            else if (field.getType() == UUID.class)
                field.set(instance, compoundTag.getUUID(attributeName));
            else if (field.getType().isEnum())
                field.set(instance, field.getType().getEnumConstants()[compoundTag.getInt(attributeName)]);
            else if (Classes.isArrayField(field, int.class))
                field.set(instance, compoundTag.getIntArray(attributeName));
            else for (final var subfield: ((ContainerAutoData)field.get(instance)).fields)
                loadFieldData(compoundTag, field.get(instance), subfield, field.getName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private String getCompoundAttributeName(@NotNull Field field, @Nullable String prefix) {
        return "%s.%s".formatted(
                BuiltInRegistries.BLOCK.getKey(block).toString(),
                (
                        prefix != null && !prefix.isEmpty() ? "%s.".formatted(Strings.camelToSnake(prefix)) : ""
                ) + Strings.camelToSnake(field.getName())
        );
    }

    private static void hasSupportedTypeOrThrow(@NotNull Field field) {
        if (field.getType() == boolean.class
                || field.getType() == short.class
                || field.getType() == int.class
                || field.getType() == long.class
                || field.getType() == float.class
                || field.getType() == double.class
                || field.getType() == String.class
                || field.getType() == UUID.class
                || field.getType().isEnum()
                || Classes.isArrayField(field, int.class)
                || Classes.isFieldLike(field, ContainerAutoData.class)) return;

        throw new RuntimeException("Compound data field `%s` has unsupported type `%s`".formatted(
                Classes.getFieldView(field),
                field.getType().getName()
        ));
    }

    @Override
    public abstract int @NotNull[] getSlotsForFace(@NotNull Direction direction);

    @Override
    public abstract boolean canTakeItemThroughFace(
            int slotIndex,
            @NotNull ItemStack itemStack,
            @NotNull Direction direction
    );

    @Override
    public abstract boolean canPlaceItemThroughFace(
            int slotIndex,
            @NotNull ItemStack itemStack,
            @Nullable Direction direction
    );

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CompoundDataField {}
}
