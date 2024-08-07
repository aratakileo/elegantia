package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.Size2iInterface;
import io.github.aratakileo.elegantia.core.math.Size2ic;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import io.github.aratakileo.elegantia.core.math.Vector2fc;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable implements Drawable {
    public final ResourceLocation texture;
    public final Size2ic textureSize;

    public Vector2fInterface uv = Vector2fc.ZERO;
    public boolean enabledBlend = false, fittedCenter = false;

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

    public @NotNull TextureDrawable setFittedCenter(boolean fittedCenter) {
        this.fittedCenter = fittedCenter;
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var textureDrawer = rectDrawer.asTextureDrawer(texture, textureSize)
                .setEnabledBlend(enabledBlend)
                .setUV(uv);

        if (fittedCenter)
            textureDrawer.renderFittedCenter();
        else
            textureDrawer.render();
    }

    public static @NotNull TextureDrawable of(@NotNull ResourceLocation texture) {
        return new TextureDrawable(texture, TextureDrawer.getTextureSize(texture));
    }
}
