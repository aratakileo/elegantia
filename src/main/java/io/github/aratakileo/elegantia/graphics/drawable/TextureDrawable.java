package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.math.Vector2fInterface;
import io.github.aratakileo.elegantia.math.Vector2fc;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class TextureDrawable implements Drawable {
    private final ResourceLocation texture;
    private final Dimension textureSize;

    public Vector2fInterface uv;
    public boolean enabledBlend = false, fittedCenter = false;

    public TextureDrawable(@NotNull ResourceLocation texture, @NotNull Dimension textureSize) {
        this(texture, textureSize, Vector2fc.ZERO);
    }

    public TextureDrawable(
            @NotNull ResourceLocation texture,
            @NotNull Dimension textureSize,
            @NotNull Vector2fInterface uv
    ) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.uv = uv;
    }

    public @NotNull TextureDrawable setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    public @NotNull TextureDrawable setEnabledBlend(boolean enabledBlend) {
        this.enabledBlend = enabledBlend;
        return this;
    }

    public @NotNull TextureDrawable setFittedCenter(boolean fittedCenter) {
        this.fittedCenter = fittedCenter;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var textureDrawer =  rectDrawer.texture(texture, textureSize).setUV(uv).setEnabledBlend(enabledBlend);

        if (fittedCenter)
            textureDrawer.renderFittedCenter();
        else
            textureDrawer.render();
    }

    public static @NotNull TextureDrawable of(@NotNull ResourceLocation texture) {
        return new TextureDrawable(texture, TextureDrawer.getTextureSize(texture));
    }

    public static @NotNull TextureDrawable of(@NotNull ResourceLocation texture, @NotNull Vector2fInterface uv) {
        return new TextureDrawable(texture, TextureDrawer.getTextureSize(texture), uv);
    }
}
