package io.github.aratakileo.elegantia.world.container;

import io.github.aratakileo.elegantia.core.Ignore;
import io.github.aratakileo.elegantia.util.Classes;
import net.minecraft.world.inventory.ContainerData;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class ContainerAutoData implements ContainerData {
    public final ArrayList<Field> fields = new ArrayList<>();

    public ContainerAutoData() {
        for (final var field: getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Ignore.class)) return;

            hasSupportedTypeOrThrow(field);
            fields.add(field);
        }
    }

    @Override
    public int get(int index) {
        try {
            final var field = fields.get(index);
            field.setAccessible(true);

            if (field.getType() == boolean.class)
                return field.getBoolean(this) ? 1 : 0;

            if (field.getType().isEnum())
                return ((Enum<?>)field.get(this)).ordinal();

            return field.getInt(this);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void set(int index, int value) {
        try {
            final var field = fields.get(index);
            field.setAccessible(true);

            if (field.getType() == boolean.class)
                field.setBoolean(this, value == 1);
            else if (field.getType().isEnum())
                field.set(this, field.getType().getEnumConstants()[value]);
            else field.setInt(this, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getCount() {
        return fields.size();
    }

    private static void hasSupportedTypeOrThrow(@NotNull Field field) {
        if (field.getType() == boolean.class || field.getType() == int.class || field.getType().isEnum())
            return;

        throw new RuntimeException("Data field `%s` has unsupported type `%s`".formatted(
                Classes.getFieldView(field),
                field.getType().getName()
        ));
    }
}
