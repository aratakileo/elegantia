package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.Size2iInterface;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable extends AbstractTextureDrawable<TextureDrawable> {
    public RenderType renderType = RenderType.DEFAULT;

    public TextureDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize
    ) {
        super(texture, textureSize);
    }

    public @NotNull TextureDrawable setRenderType(@NotNull RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    @Override
    public void render(@NotNull TextureDrawer drawer) {
        switch (renderType) {
            case DEFAULT -> drawer.render();
            case NO_REPEAT -> drawer.renderNoRepeated();
            case FIT_XY -> drawer.renderFittedXY();
            case FIT_CENTER -> drawer.renderFittedCenter();
        }
    }

    public static @NotNull TextureDrawable autoSize(@NotNull ResourceLocation texture) {
        return new TextureDrawable(texture, TextureDrawer.getTextureSize(texture));
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(@NotNull ResourceLocation texture) {
        return InitOnGet.build(texture, TextureDrawable::autoSize);
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(
            @NotNull ResourceLocation texture,
            @NotNull Vector2fInterface uv
    ) {
        return InitOnGet.build(() -> autoSize(texture).setUV(uv));
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(
            @NotNull ResourceLocation texture,
            float u,
            float v
    ) {
        return InitOnGet.build(() -> autoSize(texture).setUV(u, v));
    }

    /**
     * @see TextureDrawer
     */
    public enum RenderType {
        /**
         * Calls {@link TextureDrawer#render()}
         */
        DEFAULT,

        /**
         * Calls {@link TextureDrawer#renderNoRepeated()}
         */
        NO_REPEAT,

        /**
         * Calls {@link TextureDrawer#renderFittedXY()}
         */
        FIT_XY,

        /**
         * Calls {@link TextureDrawer#renderFittedCenter()}
         */
        FIT_CENTER
    }
}
