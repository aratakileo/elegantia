package io.github.aratakileo.elegantia.client.graphics.drawer;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.aratakileo.elegantia.core.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

public class TextureDrawer extends AbstractDrawer<TextureDrawer> {
    public final ResourceLocation texture;
    public final Size2ic textureSize;

    public Vector2fInterface uv = Vector2fc.ZERO;
    public boolean enabledBlend = false;

    public TextureDrawer(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize,
            @NotNull Rect2i bounds,
            @NotNull GuiGraphics guiGraphics
    ) {
        super(guiGraphics, bounds);
        this.texture = texture;
        this.textureSize = Size2ic.of(textureSize);
    }

    @Override
    public @NotNull TextureDrawer withNewBounds(@NotNull Rect2i bounds) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics)
                .setEnabledBlend(enabledBlend)
                .setUV(uv);
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer setUV(float u, float v) {
        this.uv = new Vector2fc(u, v);
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer setEnabledBlend(boolean enabledBlend) {
        this.enabledBlend = enabledBlend;
        return this;
    }

    /**
     * Before rendering, it scales the image in such a way as to fit it into the bounds
     * while not maintaining the original aspect ratio,
     * unlike how it happens in {@link TextureDrawer#renderFittedCenter()}
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer renderFittedXY() {
        if (enabledBlend) RenderSystem.enableBlend();

        guiGraphics.blit(
                texture,
                bounds.x,
                bounds.y,
                uv.x(),
                uv.y(),
                bounds.width,
                bounds.height,
                bounds.width,
                bounds.height
        );

        if (enabledBlend) RenderSystem.disableBlend();

        return this;
    }

    /**
     * Before rendering, it scales the image in such a way as to fit it into the bounds
     * while maintaining the original aspect ratio
     */
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
                renderBounds.width,
                renderBounds.height
        );

        if (enabledBlend) RenderSystem.disableBlend();

        return this;
    }

    /**
     * If the texture size is smaller than the size of the bounds, then the texture is not repeated,
     * unlike how it happens in {@link TextureDrawer#render()}
     */
    @SuppressWarnings("UnusedReturnValue")
    public @NotNull TextureDrawer renderNoRepeated() {
        final var renderBounds = bounds.copy();

        if (renderBounds.width > textureSize.width)
            renderBounds.width = textureSize.width;

        if (renderBounds.height > textureSize.height)
            renderBounds.height = textureSize.height;

        if (enabledBlend) RenderSystem.enableBlend();

        guiGraphics.blit(
                texture,
                bounds.getLeft(),
                bounds.getTop(),
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

    /**
     * Standard texture rendering. If the texture size is smaller than the size of the borders,
     * then the texture is repeated
     */
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

    public @NotNull RectDrawer asRectDrawer() {
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

    public static @NotNull Size2ic getTextureSize(@NotNull ResourceLocation texture) {
        if (!RenderSystem.isOnRenderThread())
            throw new IllegalStateException(
                    TextureDrawer.class.getName() + "#getTextureSize called outside the render thread"
            );

        Minecraft.getInstance().getTextureManager().getTexture(texture).bind();

        final var width = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
        final var height = GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);

        return new Size2ic(width, height);
    }
}
