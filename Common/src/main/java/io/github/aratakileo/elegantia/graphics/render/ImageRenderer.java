package io.github.aratakileo.elegantia.graphics.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.aratakileo.elegantia.graphics.ElegantiaGui;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public interface ImageRenderer extends ResourceRenderer {
    float textureU();
    float textureV();

    int sourceWidth();
    int sourceHeight();

    int areaWidth();
    int areaHeight();

    @NotNull RenderPipeline pipeline();

    @Override
    default void render(@NotNull ElegantiaGui gui, @NotNull Identifier id, int x, int y, int width, int height) {
        gui.blit(
                pipeline(),
                id,
                x, y,
                textureU(), textureV(),
                width, height,
                areaWidth(), areaHeight(),
                sourceWidth(), sourceHeight()
        );
    }
}
