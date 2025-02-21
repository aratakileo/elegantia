package io.github.aratakileo.elegantia.client.graphics;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.util.Objects;

public class ElBufferBuilder {
    public final PoseStack.Pose pose;
    public final BufferBuilder bufferBuilder;

    public ElBufferBuilder(@NotNull BufferBuilder bufferBuilder, @NotNull PoseStack.Pose pose) {
        this.bufferBuilder = bufferBuilder;
        this.pose = pose;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull ElBufferBuilder addVertex(@NotNull Vector2fInterface vec2f, int argbColor) {
        return addVertex(vec2f.x(), vec2f.y(), 0, argbColor);
    }

    public @NotNull ElBufferBuilder addVertex(@NotNull Vector2fInterface vec2f, float z, int argbColor) {
        return addVertex(vec2f.x(), vec2f.y(), z, argbColor);
    }

    public @NotNull ElBufferBuilder addVertex(@NotNull Vector3f vector3f, int argbColor) {
        return addVertex(vector3f.x, vector3f.y, vector3f.z, argbColor);
    }

    public @NotNull ElBufferBuilder addVertex(float x, float y, int argbColor) {
        return addVertex(x, y, 0, argbColor);
    }

    public @NotNull ElBufferBuilder addVertex(
            float x,
            float y,
            float z,
            int argbColor
    ) {
            bufferBuilder.addVertex(pose, x, y, z).setColor(argbColor);
        return this;
    }

    public void buildAndRender() {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        BufferUploader.drawWithShader(Objects.requireNonNull(bufferBuilder.build()));

        RenderSystem.disableBlend();
    }
}
