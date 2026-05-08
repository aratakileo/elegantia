package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.Size2iInterface;
import io.github.aratakileo.elegantia.core.math.Size2ic;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import io.github.aratakileo.elegantia.core.math.Vector2fc;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractTextureDrawable<T extends AbstractTextureDrawable<?>> implements Drawable {
    public final ResourceLocation texture;
    public final Size2ic textureSize;

    public Vector2fInterface uv = Vector2fc.ZERO;
    public boolean enabledBlend = false;

    public AbstractTextureDrawable(@NotNull ResourceLocation texture, @NotNull Size2iInterface textureSize) {
        this.texture = texture;
        this.textureSize = Size2ic.of(textureSize);
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        render(rectDrawer.texture(texture, textureSize).setEnabledBlend(enabledBlend).setUV(uv));
    }

    public abstract void render(@NotNull TextureDrawer drawer);

    @SuppressWarnings("unchecked")
    public @NotNull T setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public @NotNull T setUV(float u, float v) {
        this.uv = new Vector2fc(u, v);
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public @NotNull T setEnabledBlend(boolean enabledBlend) {
        this.enabledBlend = enabledBlend;
        return (T) this;
    }
}
