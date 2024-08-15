package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.Size2iInterface;
import io.github.aratakileo.elegantia.core.math.Size2ic;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import io.github.aratakileo.elegantia.core.math.Vector2fc;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable implements Drawable {
    public final ResourceLocation texture;
    public final Size2ic textureSize;

    public Vector2fInterface uv = Vector2fc.ZERO;
    public boolean enabledBlend = false;

    public RenderType renderType = RenderType.DEFAULT;

    public TextureDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize
    ) {
        this.texture = texture;
        this.textureSize = Size2ic.of(textureSize);
    }

    public @NotNull TextureDrawable setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    public @NotNull TextureDrawable setUV(float u, float v) {
        this.uv = new Vector2fc(u, v);
        return this;
    }

    public @NotNull TextureDrawable setEnabledBlend(boolean enabledBlend) {
        this.enabledBlend = enabledBlend;
        return this;
    }

    public @NotNull TextureDrawable setRenderType(@NotNull RenderType renderType) {
        this.renderType = renderType;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var textureDrawer = rectDrawer.texture(texture, textureSize)
                .setEnabledBlend(enabledBlend)
                .setUV(uv);

        switch (renderType) {
            case DEFAULT -> textureDrawer.render();
            case NO_REPEAT -> textureDrawer.renderNoRepeated();
            case FIT_XY -> textureDrawer.renderFittedXY();
            case FIT_CENTER -> textureDrawer.renderFittedCenter();
        }
    }

    public static @NotNull TextureDrawable autoSize(@NotNull ResourceLocation texture) {
        return new TextureDrawable(texture, TextureDrawer.getTextureSize(texture));
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(@NotNull ResourceLocation texture) {
        return InitOnGet.of(() -> autoSize(texture));
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(
            @NotNull ResourceLocation texture,
            @NotNull Vector2fInterface uv
    ) {
        return InitOnGet.of(() -> autoSize(texture).setUV(uv));
    }

    public static @NotNull InitOnGet<TextureDrawable> safeAutoSize(
            @NotNull ResourceLocation texture,
            float u,
            float v
    ) {
        return InitOnGet.of(() -> autoSize(texture).setUV(u, v));
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
