package io.github.aratakileo.elegantia.client.graphics;

import com.mojang.blaze3d.vertex.*;
import io.github.aratakileo.elegantia.client.graphics.drawer.CircleDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.*;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class ElGuiGraphics extends GuiGraphics {
    private ElGuiGraphics(@NotNull PoseStack poseStack, @NotNull MultiBufferSource.BufferSource bufferSource) {
        super(Minecraft.getInstance(), poseStack, bufferSource);
    }

    public ElGuiGraphics(@NotNull MultiBufferSource.BufferSource bufferSource) {
        super(Minecraft.getInstance(), bufferSource);
    }

    public void drawRgbLine(
            @NotNull Vector2iInterface pos1,
            @NotNull Vector2iInterface pos2,
            double thickness,
            int rgbColor
    ) {
        drawLine(pos1, pos2, thickness, rgbColor | 0xff000000);
    }

    public void drawLine(
            @NotNull Vector2iInterface pos1,
            @NotNull Vector2iInterface pos2,
            double thickness,
            int argbColor
    ) {
        drawLine(pos1.x(), pos1.y(), pos2.x(), pos2.y(), thickness, argbColor);
    }

    public void drawRgbLine(int x1, int y1, int x2, int y2, double thickness, int rgbColor) {
        drawLine(x1, y1, x2, y2, thickness, rgbColor | 0xff000000);
    }

    public void drawLine(int x1, int y1, int x2, int y2, double thickness, int argbColor) {
        final var offset = new Vector2d(x2 - x1, y2 - y1).perpendicular().normalize().mul(thickness * 0.5d);
        final var buffer = getGuiBuffer();

        buffer.addVertex((float)(x1 + offset.x), (float)(y1 + offset.y), argbColor);
        buffer.addVertex((float)(x1 - offset.x), (float)(y1 - offset.y), argbColor);
        buffer.addVertex((float)(x2 - offset.x), (float)(y2 - offset.y), argbColor);
        buffer.addVertex((float)(x2 + offset.x), (float)(y2 + offset.y), argbColor);
    }

    public void renderTooltip(
            @NotNull Component message,
            @NotNull Vector2iInterface pos
    ) {
        renderTooltip(message, pos.x(), pos.y());
    }

    public void renderTooltip(
            @NotNull Component message,
            int x,
            int y
    ) {
        renderTooltip(Minecraft.getInstance().font, message, x, y);
    }

    public void renderScrollingText(
            @NotNull Component text,
            @NotNull Rect2i bounds,
            int color
    ) {
        final var font = Minecraft.getInstance().font;
        final var textWidth = font.width(text);
        final var y = (bounds.getTop() + bounds.getBottom() - 9) / 2 + 1;

        if (textWidth > bounds.width) {
            final var widthDifference = textWidth - bounds.width;
            final var seconds = (double) Util.getMillis() / 1000.0;

            enableScissor(bounds);
            drawText(
                    text,
                    bounds.getLeft() - (int) Mth.lerp(Math.sin(Math.PI / 2 * Math.cos(
                            Math.PI * 2 * seconds / Math.max((double) widthDifference * 0.5, 3.0)
                    )) / 2.0 + 0.5, 0.0, widthDifference),
                    y,
                    color
            );
            disableScissor();
        } else drawCenteredText(text, (bounds.getLeft() + bounds.getRight()) / 2, y, color);
    }

    public void drawCenteredText(
            @NotNull Component text,
            int x,
            int y,
            int color
    ) {
        drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                color
        );
    }

    public void drawCenteredText(
            @NotNull String text,
            int x,
            int y,
            int color
    ) {
        drawCenteredString(
                Minecraft.getInstance().font,
                text,
                x,
                y,
                color
        );
    }

    public void drawText(
            @NotNull Component text,
            int x,
            int y,
            int color
    ) {
        drawText(text, x, y, color, true);
    }

    public void drawText(
            @NotNull Component text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
    }

    public void drawText(
            @NotNull String text,
            int x,
            int y,
            int color
    ) {
        drawText(text, x, y, color, true);
    }

    public void drawText(
            @NotNull String text,
            int x,
            int y,
            int color,
            boolean shadow
    ) {
        drawString(Minecraft.getInstance().font, text, x, y, color, shadow);
    }

    public void enableSquareScissor(@NotNull Vector2iInterface vec2i, int size) {
        enableScissor(vec2i.x(), vec2i.y(), size, size);
    }

    public void enableSquareScissor(int x, int y, int size) {
        enableScissor(x, y, size, size);
    }

    public void enableScissor(@NotNull Rect2i bounds) {
        super.enableScissor(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom());
    }

    public void enableScissor(@NotNull Vector2iInterface vec2i, @NotNull Size2iInterface size) {
        enableScissor(vec2i.x(), vec2i.y(), size.width(), size.height());
    }

    public void enableScissor(int x, int y, @NotNull Size2iInterface size) {
        enableScissor(x, y, size.width(), size.height());
    }

    public void enableScissor(@NotNull Vector2iInterface vec2i, int width, int height) {
        enableScissor(vec2i.x(), vec2i.y(), width, height);
    }

    @Override
    public void enableScissor(int x, int y, int width, int height) {
        super.enableScissor(x, y, x + width, y + height);
    }

    public @NotNull ElBufferBuilder getGuiBuffer() {
        return new ElBufferBuilder((BufferBuilder) bufferSource().getBuffer(RenderType.gui()), pose().last());
    }

    public @NotNull ElBufferBuilder getIndependentBuffer(@NotNull VertexFormat.Mode mode) {
        final var buffer = Tesselator.getInstance().begin(
                mode,
                DefaultVertexFormat.POSITION_COLOR
        );
        return new ElBufferBuilder(buffer, pose().last());
    }

    public @NotNull TextureDrawer textureAutoSize(@NotNull ResourceLocation location, @NotNull Rect2i bounds) {
        return new TextureDrawer(location, TextureDrawer.getTextureSize(location), bounds, this);
    }

    public @NotNull TextureDrawer textureAutoSize(
            @NotNull ResourceLocation location,
            @NotNull Vector2iInterface vec2i,
            @NotNull Size2iInterface size
    ) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                Rect2i.of(vec2i, size),
                this
        );
    }

    public @NotNull TextureDrawer textureAutoSize(
            @NotNull ResourceLocation location,
            int x,
            int y,
            @NotNull Size2iInterface size
    ) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                Rect2i.of(x, y, size),
                this
        );
    }

    public @NotNull TextureDrawer textureAutoSize(
            @NotNull ResourceLocation location,
            @NotNull Vector2iInterface vec2i,
            int width,
            int height
    ) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                Rect2i.of(vec2i, width, height),
                this
        );
    }

    public @NotNull TextureDrawer textureAutoSize(@NotNull ResourceLocation location, int x, int y, int width, int height) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                new Rect2i(x, y, width, height),
                this
        );
    }

    public @NotNull TextureDrawer textureAutoSizeSquare(
            @NotNull ResourceLocation location,
            @NotNull Vector2iInterface vec2i,
            int size
    ) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                Rect2i.square(vec2i, size),
                this
        );
    }

    public @NotNull TextureDrawer textureAutoSizeSquare(@NotNull ResourceLocation location, int x, int y, int size) {
        return new TextureDrawer(
                location,
                TextureDrawer.getTextureSize(location),
                Rect2i.square(x, y, size),
                this
        );
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            @NotNull Rect2i bounds
    ) {
        return new TextureDrawer(location, textureSize, bounds, this);
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            @NotNull Vector2iInterface vec2i,
            @NotNull Size2iInterface size
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                Rect2i.of(vec2i, size),
                this
        );
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            int x,
            int y,
            @NotNull Size2iInterface size
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                Rect2i.of(x, y, size),
                this
        );
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            @NotNull Vector2iInterface vec2i,
            int width,
            int height
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                Rect2i.of(vec2i, width, height),
                this
        );
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            int x,
            int y,
            int width,
            int height
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                new Rect2i(x, y, width, height),
                this
        );
    }

    public @NotNull TextureDrawer textureSquare(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            @NotNull Vector2iInterface vec2i,
            int size
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                Rect2i.square(vec2i, size),
                this
        );
    }

    public @NotNull TextureDrawer textureSquare(
            @NotNull ResourceLocation location,
            @NotNull Size2iInterface textureSize,
            int x,
            int y,
            int size
    ) {
        return new TextureDrawer(
                location,
                textureSize,
                Rect2i.square(x, y, size),
                this
        );
    }

    public @NotNull RectDrawer rect(@NotNull Rect2i bounds) {
        return new RectDrawer(this, bounds);
    }

    public @NotNull RectDrawer rect(@NotNull Vector2iInterface vec2i, @NotNull Size2iInterface size) {
        return RectDrawer.of(this, vec2i, size);
    }

    public @NotNull RectDrawer rect(int x, int y, @NotNull Size2iInterface size) {
        return RectDrawer.of(this, x, y, size);
    }

    public @NotNull RectDrawer rect(@NotNull Vector2iInterface vec2i, int width, int height) {
        return RectDrawer.of(this, vec2i, width, height);
    }

    public @NotNull RectDrawer rect(int x, int y, int width, int height) {
        return RectDrawer.of(this, x, y, width, height);
    }

    public @NotNull RectDrawer square(@NotNull Vector2iInterface vec2i, int size) {
        return RectDrawer.square(this, vec2i, size);
    }

    public @NotNull RectDrawer square(int x, int y, int size) {
        return RectDrawer.square(this, x, y, size);
    }

    public @NotNull CircleDrawer circle(@NotNull Vector2iInterface center, double radius) {
        return new CircleDrawer(this, center, radius);
    }

    public @NotNull CircleDrawer circle(int centerX, int centerY, double radius) {
        return new CircleDrawer(this, centerX, centerY, radius);
    }

    public static @NotNull ElGuiGraphics of(@NotNull GuiGraphics guiGraphics) {
        return new ElGuiGraphics(guiGraphics.pose(), guiGraphics.bufferSource());
    }

    public static void playSound(@NotNull SoundEvent sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, 1f));
    }

    public static void playSound(@NotNull SoundEvent sound, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound, volume));
    }

    public static void playSound(@NotNull Holder<SoundEvent> sound) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), 1f));
    }

    public static void playSound(@NotNull Holder<SoundEvent> sound, float volume) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(sound.value(), volume));
    }

    public static void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1f));
    }

    public static float getDeltaTime() {
        return Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
    }
}
