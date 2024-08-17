package io.github.aratakileo.elegantia.client.graphics.drawable;

import io.github.aratakileo.elegantia.client.graphics.drawer.RectDrawer;
import io.github.aratakileo.elegantia.client.graphics.drawer.TextureDrawer;
import io.github.aratakileo.elegantia.core.math.*;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ElasticTextureDrawable implements Drawable {
    public final ResourceLocation source;
    public final Size2ic textureSize;
    public final int borderWidth;

    public @NotNull Vector2fInterface uv = Vector2fc.ZERO;

    public ElasticTextureDrawable(
            @NotNull ResourceLocation source,
            @NotNull Size2iInterface textureSize,
            int borderWidth
    ) {
        this.source = source;
        this.textureSize = Size2ic.of(textureSize);
        this.borderWidth = borderWidth;
    }

    public @NotNull ElasticTextureDrawable setUV(@NotNull Vector2fInterface uv) {
        this.uv = uv;
        return this;
    }

    public @NotNull ElasticTextureDrawable setUV(float u, float v) {
        this.uv = new Vector2fc(u, v);
        return this;
    }

    @Override
    public void render(@NotNull RectDrawer rectDrawer) {
        final var centerAndSideSegmentSize = rectDrawer.bounds.getSize().shrink(borderWidth);
        final var centerSize = centerAndSideSegmentSize.shrink(borderWidth);
        final var centerAndSideSegmentTextureSize = textureSize.shrink(borderWidth);
        final var centerTextureSize = centerAndSideSegmentTextureSize.shrink(borderWidth);

        final var textureDrawer = rectDrawer.texture(source, textureSize).setUV(uv);

        textureDrawer.renderSquare(0, 0, 0, 0, borderWidth)
                .renderSquare(
                        0,
                        centerAndSideSegmentSize.height,
                        0,
                        centerAndSideSegmentTextureSize.height,
                        borderWidth
                ).renderSquare(
                        centerAndSideSegmentSize.asVec2i(),
                        centerAndSideSegmentTextureSize.asVec2f(),
                        borderWidth
                ).renderSquare(
                        centerAndSideSegmentSize.width,
                        0,
                        centerAndSideSegmentTextureSize.width,
                        0,
                        borderWidth
                );

        final var horizontalCenterSegments = Math.ceil((double)centerSize.width / (double)centerTextureSize.width);
        final var verticalCenterSegments = Math.ceil((double)centerSize.height / (double)centerTextureSize.height);

        final var minCenterTextureSize = centerTextureSize.min(centerSize);

        for (var xIndex = 0; xIndex < horizontalCenterSegments; xIndex++) {
            final var xOffset = borderWidth + minCenterTextureSize.width * xIndex;
            final var lastXSegment = xIndex == horizontalCenterSegments - 1;
            final var finalWidth = lastXSegment
                    ? rectDrawer.bounds.width - xOffset - borderWidth
                    : minCenterTextureSize.width;

            for (var yIndex = 0; yIndex < verticalCenterSegments; yIndex++) {
                final var yOffset = borderWidth + minCenterTextureSize.height * yIndex;
                final var lastYSegment = yIndex == verticalCenterSegments - 1;
                final var finalHeight = lastYSegment
                        ? rectDrawer.bounds.height - yOffset - borderWidth
                        : minCenterTextureSize.height;

                textureDrawer.render(
                        xOffset,
                        yOffset,
                        borderWidth,
                        borderWidth,
                        finalWidth,
                        finalHeight
                );

                if (xOffset == borderWidth)
                    textureDrawer.render(
                            0,
                            yOffset,
                            0,
                            borderWidth,
                            borderWidth,
                            finalHeight
                    );

                if (lastXSegment) {
                    textureDrawer.render(
                            centerAndSideSegmentSize.width,
                            yOffset,
                            centerAndSideSegmentTextureSize.width,
                            borderWidth,
                            borderWidth,
                            finalHeight
                    );
                }
            }

            textureDrawer.render(
                    xOffset,
                    0,
                    borderWidth,
                    0,
                    finalWidth,
                    borderWidth
            );

            textureDrawer.render(
                    xOffset,
                    centerAndSideSegmentSize.height,
                    borderWidth,
                    centerAndSideSegmentTextureSize.height,
                    finalWidth,
                    borderWidth
            );
        }
    }

    public static @NotNull ElasticTextureDrawable autoSize(
            @NotNull ResourceLocation source,
            int borderWidth
    ) {
       return new ElasticTextureDrawable(source, TextureDrawer.getTextureSize(source), borderWidth);
    }

    public static @NotNull InitOnGet<ElasticTextureDrawable> safeAutoSize(
            @NotNull ResourceLocation source,
            int borderWidth
    ) {
       return InitOnGet.of(
               () -> new ElasticTextureDrawable(source, TextureDrawer.getTextureSize(source), borderWidth)
       );
    }

    public static @NotNull InitOnGet<ElasticTextureDrawable> safeAutoSize(
            @NotNull ResourceLocation source,
            int borderWidth,
            @NotNull Vector2fInterface uv
    ) {
       return InitOnGet.of(
               () -> new ElasticTextureDrawable(source, TextureDrawer.getTextureSize(source), borderWidth).setUV(uv)
       );
    }

    public static @NotNull InitOnGet<ElasticTextureDrawable> safeAutoSize(
            @NotNull ResourceLocation source,
            int borderWidth,
            float u,
            float v
    ) {
       return InitOnGet.of(
               () -> new ElasticTextureDrawable(source, TextureDrawer.getTextureSize(source), borderWidth).setUV(u, v)
       );
    }

    public static @NotNull ElasticTextureDrawable of(@NotNull TextureDrawable textureDrawable, int borderWidth) {
        return new ElasticTextureDrawable(
                textureDrawable.texture,
                textureDrawable.textureSize,
                borderWidth
        ).setUV(textureDrawable.uv);
    }

    public static @NotNull InitOnGet<ElasticTextureDrawable> of(
            @NotNull InitOnGet<TextureDrawable> textureDrawableGetter,
            int borderWidth
    ) {
        return InitOnGet.of(() -> of(textureDrawableGetter.get(), borderWidth));
    }
}
