package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.*;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TexturedProgressDrawable implements Drawable {
    protected final ResourceLocation texture;
    protected final Size2ic textureSize;
    protected final Direction direction;
    protected final Supplier<Float> progressGetter;

    public Vector2fInterface uv = Vector2fc.ZERO;

    public TexturedProgressDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        this.texture = texture;
        this.textureSize = Size2ic.of(textureSize);
        this.direction = direction;
        this.progressGetter = progressGetter;
    }

    public @NotNull TexturedProgressDrawable setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    public @NotNull TexturedProgressDrawable setUV(float u, float v) {
        this.uv = new Vector2fc(u, v);
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var renderAreaSize = rectDrawer.bounds.getSize();
        final var newBounds = rectDrawer.bounds.copy();
        final var newUV = Vector2f.of(uv);
        final var renderAreaSizeScaledByProgress = renderAreaSize.scale(progressGetter.get());
        final var remainedRenderAreaSize = renderAreaSize.sub(renderAreaSizeScaledByProgress);

        switch (direction) {
            case TOP -> {
                newUV.y += remainedRenderAreaSize.height;
                newBounds.y += remainedRenderAreaSize.height;
                newBounds.height = renderAreaSizeScaledByProgress.height;
            }

            case BOTTOM -> newBounds.height = renderAreaSizeScaledByProgress.height;

            case LEFT -> {
                newUV.x += remainedRenderAreaSize.width;
                newBounds.x += remainedRenderAreaSize.width;
                newBounds.width = renderAreaSizeScaledByProgress.width;
            }

            case RIGHT -> newBounds.width = renderAreaSizeScaledByProgress.width;
        }

        rectDrawer.textureDrawer(texture, textureSize, newBounds)
                .setUV(newUV)
                .render();
    }

    public static @NotNull TexturedProgressDrawable of(
            @NotNull ResourceLocation texture,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        return new TexturedProgressDrawable(
                texture,
                TextureDrawer.getTextureSize(texture),
                direction,
                progressGetter
        );
    }

    public enum Direction {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }
}
