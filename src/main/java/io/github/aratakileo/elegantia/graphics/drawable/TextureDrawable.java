package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable extends Drawable {
    private final ResourceLocation texture;
    private final boolean fitCenter;

    public TextureDrawable(@NotNull String path) {
        this(path, false);
    }

    public TextureDrawable(@NotNull String path, boolean fitCenter) {
        this.texture = new ResourceLocation(path);
        this.fitCenter = fitCenter;
    }

    public TextureDrawable(@NotNull String namespace, @NotNull String path) {
        this(namespace, path, false);
    }

    public TextureDrawable(@NotNull String namespace, @NotNull String path, boolean fitCenter) {
        this.texture = new ResourceLocation(namespace, path);
        this.fitCenter = fitCenter;
    }

    @Override
    protected void render(@NotNull RectDrawer rectDrawer, float dt) {
        if (fitCenter) rectDrawer.renderFittedCenterTexture(texture);
        else rectDrawer.renderTexture(texture);
    }
}
