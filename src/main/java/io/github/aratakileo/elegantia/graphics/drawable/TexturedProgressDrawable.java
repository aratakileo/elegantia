package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.math.Vector2f;
import io.github.aratakileo.elegantia.math.Vector2fInterface;
import io.github.aratakileo.elegantia.math.Vector2fc;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.function.Supplier;

public class TexturedProgressDrawable implements Drawable {
    protected final ResourceLocation texture;
    protected final Dimension textureSize;
    protected final Direction direction;
    protected final Supplier<Float> progressGetter;

    public Vector2fInterface uv = Vector2fc.ZERO;

    public TexturedProgressDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Dimension textureSize,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.direction = direction;
        this.progressGetter = progressGetter;
    }

    public @NotNull TexturedProgressDrawable setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var newBounds = rectDrawer.bounds.copy().setSize(textureSize);
        final var newUV = Vector2f.of(uv);

        final var progressedTextureSize = new Dimension(textureSize);
        progressedTextureSize.width *= progressGetter.get();
        progressedTextureSize.height *= progressGetter.get();

        final var remainedTextureSize = new Dimension(
                textureSize.width - progressedTextureSize.width,
                textureSize.height - progressedTextureSize.height
        );

        switch (direction) {
            case TOP -> {
                newUV.y += remainedTextureSize.height;
                newBounds.y -= remainedTextureSize.height;
                newBounds.height = progressedTextureSize.height;
            }

            case BOTTOM -> newBounds.height = remainedTextureSize.height;

            case LEFT -> {
                newUV.x += remainedTextureSize.width;
                newBounds.x -= remainedTextureSize.width;
                newBounds.width = progressedTextureSize.width;
            }

            case RIGHT -> newBounds.width = remainedTextureSize.width;
        }

        rectDrawer.asTextureDrawer(texture, textureSize, newBounds)
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
