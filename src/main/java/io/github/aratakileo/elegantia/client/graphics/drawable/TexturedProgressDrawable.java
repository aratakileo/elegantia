package io.github.aratakileo.elegantia.client.graphics.drawable;

import com.google.common.util.concurrent.AtomicDouble;
import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.*;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class TexturedProgressDrawable extends AbstractTextureDrawable<TexturedProgressDrawable> {
    protected final Direction direction;
    protected final Supplier<Float> progressGetter;

    public TexturedProgressDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        super(texture, textureSize);
        this.direction = direction;
        this.progressGetter = progressGetter;
    }

    @Override
    public void render(@NotNull TextureDrawer drawer) {
        final var renderAreaSize = drawer.bounds.getSize();
        final var newBounds = drawer.bounds.copy();
        final var newUV = Vector2f.of(uv);
        final var renderAreaSizeScaledByProgress = renderAreaSize.scale(progressGetter.get());
        final var remainedRenderAreaSize = renderAreaSize.shrink(renderAreaSizeScaledByProgress);

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

        drawer.withNewBounds(newBounds).setUV(newUV).render();
    }

    public static @NotNull TexturedProgressDrawable autoSize(
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

    public static <T extends AbstractTextureDrawable<?>> @NotNull TexturedProgressDrawable of(
            @NotNull T textureDrawable,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        return new TexturedProgressDrawable(
                textureDrawable.texture,
                textureDrawable.textureSize,
                direction,
                progressGetter
        ).setUV(textureDrawable.uv);
    }

    public static <T extends AbstractTextureDrawable<?>> @NotNull InitOnGet<TexturedProgressDrawable> of(
            @NotNull InitOnGet<T> textureDrawable,
            @NotNull Direction direction,
            @NotNull Supplier<Float> progressGetter
    ) {
        return InitOnGet.buildOn(textureDrawable, drawable -> new TexturedProgressDrawable(
                drawable.texture,
                drawable.textureSize,
                direction,
                progressGetter
        ).setUV(drawable.uv));
    }

    /**
     * Returns a supplier that automatically generates a smooth increase in progress
     * according to the specified animation speed
     */
    public static @NotNull Supplier<Float> demoProgressAnimation(float speed) {
        final var counter = new AtomicDouble(0);

        return () -> {
            if (counter.addAndGet(ElGuiGraphics.getDeltaTime() * (speed / 100f)) > 1d)
                counter.set(0);

            return counter.floatValue();
        };
    }

    public enum Direction {
        TOP,
        BOTTOM,
        LEFT,
        RIGHT
    }
}
