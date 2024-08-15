package io.github.aratakileo.elegantia.client.graphics.drawer;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.*;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RectDrawer extends AbstractRectDrawer<RectDrawer> {
    public @NotNull CornersRadius cornersRadius = CornersRadius.EMPTY;

    public RectDrawer(@NotNull ElGuiGraphics guiGraphics, @NotNull Rect2i bounds) {
        super(guiGraphics, bounds);
    }

    public @NotNull RectDrawer setCornersRadius(double radius) {
        cornersRadius = CornersRadius.createSameRadius(radius);
        return this;
    }

    public @NotNull RectDrawer setCornersRadius(@NotNull CornersRadius cornersRadius) {
        this.cornersRadius = cornersRadius;
        return this;
    }

    @Override
    public @NotNull RectDrawer withNewBounds(@NotNull Rect2i bounds) {
        return new RectDrawer(guiGraphics, bounds).setCornersRadius(cornersRadius);
    }

    public @NotNull RectDrawer drawRgb(int rgbColor) {
        return draw(rgbColor | 0xff000000);
    }

    public @NotNull RectDrawer draw(int argbColor) {
        if (argbColor == 0x0) return this;

        if (cornersRadius.isEmpty()) {
            guiGraphics.fill(bounds.getLeft(), bounds.getTop(), bounds.getRight(), bounds.getBottom(), argbColor);
            return this;
        }

        final var lastPose = guiGraphics.pose().last();
        final var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        drawCorner(buffer, lastPose, argbColor, Corner.LEFT_TOP);
        drawCorner(buffer, lastPose, argbColor, Corner.LEFT_BOTTOM);
        drawCorner(buffer, lastPose, argbColor, Corner.RIGHT_BOTTOM);
        drawCorner(buffer, lastPose, argbColor, Corner.RIGHT_TOP);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(buffer.build());
        RenderSystem.disableBlend();

        return this;
    }

    private void drawCorner(
            @NotNull BufferBuilder buffer,
            @NotNull PoseStack.Pose lastPose,
            int argbColor,
            @NotNull Corner corner
    ) {
        final var radius = cornersRadius.getRadius(corner);
        final var segments = Math.round(2d * Math.PI * radius);

        if (segments == 0) {
            final var vertexPos = bounds.getCornerPos(corner).asVec2f().asVector3f(0);

            buffer.addVertex(lastPose, vertexPos).setColor(argbColor);

            return;
        }

        final var radiusVec = Vector2dc.createXY(radius);
        final var center = corner.getCenter(bounds, cornersRadius);
        final var angleStep = (float)Corner.SPAN_ANGLE / (float)segments;

        for (var i = segments; i >= 0; i--) {
            final var angle = corner.startAngle + i * angleStep;
            final var theta = Math.toRadians(angle);
            final var polarPos = radiusVec.mul(Math.cos(theta), Math.sin(theta));
            final var vertexPos = center.add(polarPos).asVec2f().asVector3f(0);

            buffer.addVertex(lastPose, vertexPos).setColor(argbColor);
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbGradient(int rgbColorStart, int rgbColorEnd, @NotNull GradientDirection direction) {
        return drawGradient(rgbColorStart | 0xff000000, rgbColorEnd | 0xff000000, direction);
    }

    public @NotNull RectDrawer drawGradient(
            int argbColorStart,
            int argbColorEnd,
            @NotNull GradientDirection direction
    ) {
        if (argbColorStart == 0x0 || argbColorEnd == 0x0) return this;

        return switch (direction) {
            case HORIZONTAL -> drawGradient(argbColorStart, argbColorStart, argbColorEnd, argbColorEnd);
            case VERTICAL -> drawGradient(argbColorStart, argbColorEnd, argbColorEnd, argbColorStart);
            case DIAGONAL -> drawGradient(argbColorEnd, argbColorStart, argbColorEnd, argbColorStart);
            case CORNER_LEFT_TOP -> drawGradient(argbColorStart, argbColorEnd, argbColorEnd, argbColorEnd);
            case CORNER_LEFT_BOTTOM -> drawGradient(argbColorEnd, argbColorStart, argbColorEnd, argbColorEnd);
            case CORNER_RIGHT_BOTTOM -> drawGradient(argbColorEnd, argbColorEnd, argbColorStart, argbColorEnd);
            case CORNER_RIGHT_TOP -> drawGradient(argbColorEnd, argbColorEnd, argbColorEnd, argbColorStart);
        };
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbGradient(
            int rgbTopLeftColor,
            int rgbBottomLeftColor,
            int rgbBottomRightColor,
            int rgbTopRightColor
    ) {
        return drawGradient(
                rgbTopLeftColor | 0xff000000,
                rgbBottomLeftColor | 0xff000000,
                rgbBottomRightColor | 0xff000000,
                rgbTopRightColor | 0xff000000
        );
    }

    public @NotNull RectDrawer drawGradient(
            int argbTopLeftColor,
            int argbBottomLeftColor,
            int argbBottomRightColor,
            int argbTopRightColor
    ) {
        if (
                argbTopLeftColor == 0x0
                        || argbBottomLeftColor == 0x0
                        || argbBottomRightColor == 0x0
                        || argbTopRightColor == 0x0
        ) return this;

        final var lastPose = guiGraphics.pose().last();

        if (cornersRadius.isEmpty()) {
            guiGraphics.bufferSource().getBuffer(RenderType.gui())
                    .addVertex(lastPose, bounds.getLeft(), bounds.getTop(), 0)
                    .setColor(argbTopLeftColor)
                    .addVertex(lastPose, bounds.getLeft(), bounds.getBottom(), 0)
                    .setColor(argbBottomLeftColor)
                    .addVertex(lastPose, bounds.getRight(), bounds.getBottom(), 0)
                    .setColor(argbBottomRightColor)
                    .addVertex(lastPose, bounds.getRight(), bounds.getTop(), 0)
                    .setColor(argbTopRightColor);
            return this;
        }

        final var buffer = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_FAN, DefaultVertexFormat.POSITION_COLOR);

        drawCorner(buffer, lastPose, argbTopLeftColor, Corner.LEFT_TOP);
        drawCorner(buffer, lastPose, argbBottomLeftColor, Corner.LEFT_BOTTOM);
        drawCorner(buffer, lastPose, argbBottomRightColor, Corner.RIGHT_BOTTOM);
        drawCorner(buffer, lastPose, argbTopRightColor, Corner.RIGHT_TOP);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(buffer.build());
        RenderSystem.disableBlend();

        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public @NotNull RectDrawer drawRgbStroke(int rgbColor, int thickness) {
        return drawStroke(rgbColor | 0xff000000, thickness);
    }

    public @NotNull RectDrawer drawStroke(int argbColor, int thickness) {
        if (argbColor == 0x0 || thickness == 0) return this;

        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getTop(),
                bounds.getRight(),
                bounds.getTop() + thickness,
                argbColor
        );
        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getBottom() - thickness,
                bounds.getRight(),
                bounds.getBottom(),
                argbColor
        );
        guiGraphics.fill(
                bounds.getLeft(),
                bounds.getTop() + thickness,
                bounds.getLeft() + thickness,
                bounds.getBottom() - thickness,
                argbColor
        );
        guiGraphics.fill(
                bounds.getRight() - thickness,
                bounds.getTop() + thickness,
                bounds.getRight(),
                bounds.getBottom() - thickness,
                argbColor
        );

        return this;
    }

    public @NotNull TextureDrawer textureAutoSize(@NotNull ResourceLocation texture) {
        return new TextureDrawer(texture, TextureDrawer.getTextureSize(texture), bounds, guiGraphics);
    }

    public @NotNull TextureDrawer texture(@NotNull ResourceLocation texture, @NotNull Size2iInterface textureSize) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics);
    }

    public @NotNull TextureDrawer textureAutoSize(@NotNull ResourceLocation texture, @NotNull Rect2i bounds) {
        return new TextureDrawer(texture, TextureDrawer.getTextureSize(texture), bounds, guiGraphics);
    }

    public @NotNull TextureDrawer texture(
            @NotNull ResourceLocation texture,
            @NotNull Size2iInterface textureSize,
            @NotNull Rect2i bounds
    ) {
        return new TextureDrawer(texture, textureSize, bounds, guiGraphics);
    }

    @Override
    public String toString() {
        return "RectDrawer{%s, %s, %s, %s}".formatted(
                bounds.x,
                bounds.y,
                bounds.width,
                bounds.height
        );
    }

    public static RectDrawer of(
            @NotNull ElGuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            @NotNull Size2iInterface size
    ) {
        return new RectDrawer(guiGraphics, Rect2i.of(pos, size));
    }

    public static RectDrawer of(
            @NotNull ElGuiGraphics guiGraphics,
            @NotNull Vector2iInterface pos,
            int width,
            int height
    ) {
        return new RectDrawer(guiGraphics, Rect2i.of(pos, width, height));
    }

    public static RectDrawer of(@NotNull ElGuiGraphics guiGraphics, int x, int y, @NotNull Size2iInterface size) {
        return new RectDrawer(guiGraphics, Rect2i.of(x, y, size));
    }

    public static RectDrawer of(@NotNull ElGuiGraphics guiGraphics, int x, int y, int width, int height) {
        return new RectDrawer(guiGraphics, new Rect2i(x, y, width, height));
    }

    public static RectDrawer square(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2iInterface pos, int size) {
        return new RectDrawer(guiGraphics, Rect2i.square(pos, size));
    }

    public static RectDrawer square(@NotNull ElGuiGraphics guiGraphics, int x, int y, int size) {
        return new RectDrawer(guiGraphics, Rect2i.square(x, y, size));
    }

    public enum GradientDirection {
        HORIZONTAL,
        VERTICAL,
        DIAGONAL,
        CORNER_LEFT_TOP,
        CORNER_LEFT_BOTTOM,
        CORNER_RIGHT_BOTTOM,
        CORNER_RIGHT_TOP
    }

    public static final class CornersRadius {
        public final double leftTop, leftBottom, rightBottom, rightTop;

        public final static CornersRadius EMPTY = new CornersRadius(0, 0, 0, 0);

        public CornersRadius(double leftTop, double leftBottom, double rightBottom, double rightTop) {
            Preconditions.checkArgument(leftTop >= 0, "leftTop must be greater or equal 0");
            Preconditions.checkArgument(leftBottom >= 0, "leftBottom must be greater or equal 0");
            Preconditions.checkArgument(rightBottom >= 0, "rightBottom must be greater or equal 0");
            Preconditions.checkArgument(rightTop >= 0, "rightTop must be greater or equal 0");

            this.leftTop = leftTop;
            this.leftBottom = leftBottom;
            this.rightBottom = rightBottom;
            this.rightTop = rightTop;
        }

        public boolean isEmpty() {
            return leftTop == 0 && leftBottom == 0 && rightBottom == 0 && rightTop == 0;
        }

        public double getRadius(@NotNull Corner corner) {
            return switch (corner) {
                case LEFT_TOP -> leftTop;
                case LEFT_BOTTOM -> leftBottom;
                case RIGHT_BOTTOM -> rightBottom;
                case RIGHT_TOP -> rightTop;
            };
        }

        public double getFittedInRadius(@NotNull Corner corner, @NotNull Size2iInterface size) {
            return Math.min(getRadius(corner), size.asVec2d().min());
        }

        public static @NotNull CornersRadius createSameRadius(double radius) {
            return new CornersRadius(radius, radius, radius, radius);
        }

        @Override
        public String toString() {
            return "CornersRadius{leftTop=%s, leftBottom=%s, rightBottom=%s, rightTop=%s}".formatted(
                    leftTop,
                    leftBottom,
                    rightBottom,
                    rightTop
            );
        }
    }
}
