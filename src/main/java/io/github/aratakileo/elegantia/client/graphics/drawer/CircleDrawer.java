package io.github.aratakileo.elegantia.client.graphics.drawer;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.Vector2dc;
import io.github.aratakileo.elegantia.core.math.Vector2iInterface;
import net.minecraft.client.renderer.GameRenderer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class CircleDrawer {
    private final ElGuiGraphics guiGraphics;
    private final int centerX, centerY;
    private final double radius;
    private double angleFrom = 0, angleTo = 360;

    public double smoothness = 1;

    public CircleDrawer(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2iInterface center, double radius) {
        this(guiGraphics, center.x(), center.y(), radius);
    }

    public CircleDrawer(@NotNull ElGuiGraphics guiGraphics, int centerX, int centerY, double radius) {
        this.guiGraphics = guiGraphics;
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius = radius;
    }

    public @NotNull CircleDrawer setAngleSpan(double angleFrom, double angleTo) {
        Preconditions.checkArgument(angleFrom < angleTo, "angleFrom must be less than angleTo");

        this.angleFrom = angleFrom;
        this.angleTo = angleTo;

        return this;
    }

    public @NotNull CircleDrawer setFullAngleSpan() {
        angleFrom = 0;
        angleTo = 360;

        return this;
    }

    public @NotNull CircleDrawer setSmoothness(double smoothness) {
        this.smoothness = smoothness;
        return this;
    }

    public @NotNull CircleDrawer drawRgb(int rgbColor) {
        return draw(rgbColor | 0xff000000);
    }

    public @NotNull CircleDrawer draw(int argbColor) {
        final var radiusVec = new Vector2dc(radius, radius);

        _forEachVertex(
                VertexFormat.Mode.TRIANGLE_FAN,
                (buffer, lastPose) -> buffer.addVertex(lastPose, centerX, centerY, 0).setColor(argbColor),
                (buffer, lastPose, theta) -> {
                    final var polarPos = radiusVec.mul(Math.cos(theta), Math.sin(theta)).asVec2f();
                    final var vertexPos = polarPos.neg().add(centerX, centerY);

                    buffer.addVertex(lastPose, vertexPos.asVector3f(0)).setColor(argbColor);
                }
        );

        return this;
    }

    public @NotNull CircleDrawer drawRgbStoke(int rgbColor, double thickness) {
        return drawStoke(rgbColor | 0xff000000, thickness);
    }

    public @NotNull CircleDrawer drawStoke(int argbColor, double thickness) {
        final var halfThickness = thickness / 2d;
        final var innerRadiusVec = Vector2dc.createXY(radius - halfThickness);
        final var outerRadiusVec = Vector2dc.createXY(radius + halfThickness);

        _forEachVertex(VertexFormat.Mode.TRIANGLE_STRIP, null, (buffer, lastPose, theta) -> {
            final var innerPolarPos = innerRadiusVec.mul(Math.cos(theta), Math.sin(theta)).asVec2f();
            final var innerVertexPos = innerPolarPos.neg().add(centerX, centerY);
            final var outerPolarPos = outerRadiusVec.mul(Math.cos(theta), Math.sin(theta)).asVec2f();
            final var outerVertexPos = outerPolarPos.neg().add(centerX, centerY);

            buffer.addVertex(lastPose, innerVertexPos.asVector3f(0)).setColor(argbColor);
            buffer.addVertex(lastPose, outerVertexPos.asVector3f(0)).setColor(argbColor);
        });

        return this;
    }

    private void _forEachVertex(
            @NotNull VertexFormat.Mode mode,
            @Nullable BiConsumer<BufferBuilder, PoseStack.Pose> prepareConsumer,
            @NotNull CircleVertexConsumer consumer
    ) {
        final var lastPose = guiGraphics.pose().last();
        final var buffer = Tesselator.getInstance().begin(mode, DefaultVertexFormat.POSITION_COLOR);

        final var circumference = 2d * Math.PI * radius;
        final var segments = Math.max(4, (int)Math.ceil(circumference * smoothness));
        final var angleStep = Math.toRadians((angleTo - angleFrom) / segments);

        if (prepareConsumer != null)
            prepareConsumer.accept(buffer, lastPose);

        for (var i = segments; i >= 0; i--) {
            final var theta = Math.toRadians(angleFrom) + i * angleStep;

            consumer.accept(buffer, lastPose, theta);
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        BufferUploader.drawWithShader(buffer.build());
        RenderSystem.disableBlend();
    }

    @Override
    public String toString() {
        return "CircleDrawer{center={%d, %d}, radius=%.2f, angleSpan={%.2f, %.2f}%s, smoothness=%.2f}".formatted(
                centerX,
                centerY,
                radius,
                angleFrom,
                angleTo,
                angleFrom == 0 && angleTo == 360 ? " (FULL)" : "",
                smoothness
        );
    }

    private interface CircleVertexConsumer {
        void accept(@NotNull BufferBuilder buffer, @NotNull PoseStack.Pose lastPose, double theta);
    }
}
