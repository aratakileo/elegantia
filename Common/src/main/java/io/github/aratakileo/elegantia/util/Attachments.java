package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.framework.data.AttachmentHolder;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

public final class Attachments {
    private Attachments() {}

    public static @NotNull AttachmentHolder<?> at(@NotNull Entity entity) {
        return ((AttachmentHolder<?>) entity);
    }
}
