package io.github.aratakileo.elegantia.graphics;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.aratakileo.elegantia.math.Rect2i;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class RectVertexConsumer {
    private final static float DEFAULT_Z_LEVEL = 0;

    private final Rect2i bounds;
    private final VertexConsumer buffer;
    private final Matrix4f pose;

    public RectVertexConsumer(
            @NotNull Rect2i bounds,
            @NotNull VertexConsumer buffer,
            @NotNull Matrix4f pose
    ) {
        this.bounds = bounds;
        this.buffer = buffer;
        this.pose = pose;
    }

    public @NotNull VertexConsumer vertex(int cornerIndex) {
        return switch (cornerIndex) {
            case 0 -> buffer.vertex(pose, bounds.getLeft(), bounds.getTop(), DEFAULT_Z_LEVEL);
            case 1 -> buffer.vertex(pose, bounds.getLeft(), bounds.getBottom(), DEFAULT_Z_LEVEL);
            case 2 -> buffer.vertex(pose, bounds.getRight(), bounds.getBottom(), DEFAULT_Z_LEVEL);
            case 3 -> buffer.vertex(pose, bounds.getRight(), bounds.getTop(), DEFAULT_Z_LEVEL);
            default -> throw new IllegalArgumentException();
        };
    }

    public void coloredVertex(int cornerIndex, int color) {
        vertex(cornerIndex).color(color).endVertex();
    }
}
