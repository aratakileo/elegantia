package io.github.aratakileo.elegantia.graphics.drawer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.math.Vector2fInterface;
import io.github.aratakileo.elegantia.math.Vector2fc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class TextureDrawer {
    public final ResourceLocation texture;
    public final Dimension textureSize;
    public final Rect2i bounds;
    public final GuiGraphics guiGraphics;

    public Vector2fInterface uv = Vector2fc.ZERO;
    public boolean enabledBlend = false;

    public TextureDrawer(
            @NotNull ResourceLocation texture,
            @NotNull Dimension textureSize,
            @NotNull Rect2i bounds,
            @NotNull GuiGraphics guiGraphics
    ) {
        this.texture = texture;
        this.textureSize = textureSize;
        this.bounds = bounds;
        this.guiGraphics = guiGraphics;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer setEnabledBlend(boolean enabledBlend) {
        this.enabledBlend = enabledBlend;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer renderFittedCenter() {
        final var renderBounds = bounds.copy();

        if (textureSize.width < textureSize.height) {
            renderBounds.width *= ((float) textureSize.width / textureSize.height);
            renderBounds.x += (bounds.width - renderBounds.width) / 2;
        } else if (textureSize.height < textureSize.width) {
            renderBounds.height *= ((float) textureSize.height / textureSize.width);
            renderBounds.y += (bounds.height - renderBounds.height) / 2;
        }

        if (enabledBlend) RenderSystem.enableBlend();

        guiGraphics.blit(
                texture,
                renderBounds.x,
                renderBounds.y,
                uv.x(),
                uv.y(),
                renderBounds.width,
                renderBounds.height,
                textureSize.width,
                textureSize.height
        );

        if (enabledBlend) RenderSystem.disableBlend();

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer render() {
        if (enabledBlend) RenderSystem.enableBlend();

        guiGraphics.blit(
                texture,
                bounds.getLeft(),
                bounds.getTop(),
                uv.x(),
                uv.y(),
                bounds.width,
                bounds.height,
                textureSize.width,
                textureSize.height
        );

        if (enabledBlend) RenderSystem.disableBlend();

        return this;
    }

    public @NotNull RectDrawer rect() {
        return new RectDrawer(guiGraphics, bounds);
    }

    @Override
    public String toString() {
        return "TextureDrawer{texture=%s,textureSize=%s,bounds=%s,uv=%s,enabledBlend=%s}".formatted(
                texture,
                textureSize,
                bounds,
                uv,
                enabledBlend
        );
    }

    public static @NotNull Dimension getTextureSize(@NotNull ResourceLocation texture) {
        final var textureBinding = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        Minecraft.getInstance().getTextureManager().bindForSetup(texture);

        final var width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        final var height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureBinding);

        return new Dimension(width, height);
    }
}
