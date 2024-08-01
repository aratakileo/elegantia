package io.github.aratakileo.elegantia.graphics.drawable;

import io.github.aratakileo.elegantia.graphics.RectDrawer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TextureDrawable implements Drawable {
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
    public void render(@NotNull RectDrawer rectDrawer, float dt) {
        if (fitCenter)
            rectDrawer.renderFittedCenterTexture(texture);
        else rectDrawer.renderTexture(texture);
    }
}
