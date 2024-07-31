package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import io.github.aratakileo.elegantia.util.Namespace;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable extends Drawable {
    private final ResourceLocation texture;
    private final boolean fitCenter;

    public TextureDrawable(@NotNull ResourceLocation texture) {
        this(texture, false);
    }

    public TextureDrawable(@NotNull ResourceLocation texture, boolean fitCenter) {
        this.texture = texture;
        this.fitCenter = fitCenter;
    }

    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        if (fitCenter) rectDrawer.renderFittedCenterTexture(texture);
        else rectDrawer.renderTexture(texture);
    }

    public static @NotNull TextureDrawable of(@NotNull Namespace namespace, @NotNull String path) {
        return of(namespace, path, false);
    }

    public static @NotNull TextureDrawable of(@NotNull Namespace namespace, @NotNull String path, boolean fitCenter) {
        return new TextureDrawable(namespace.getLocation(path), fitCenter);
    }
}
