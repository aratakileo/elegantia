package io.github.aratakileo.elegantia.client.graphics.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.aratakileo.elegantia.client.event.GuiTicker;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

public final class GifRenderer implements GuiTicker.Listener, ImageRenderer {
    private final int frameHeight, frameWidth, duration;
    private final int[] frameDurations;

    private int frameIndex;
    private long elapsedDuration, durationThreshold;

    public GifRenderer(int frameWidth, int frameHeight, int duration, int[] frameDurations) {
        this.frameHeight = frameHeight;
        this.frameWidth = frameWidth;
        this.duration = duration;
        this.frameDurations = frameDurations;

        resetAnimation();
    }

    private void resetAnimation() {
        frameIndex = 0;
        elapsedDuration = 0;
        durationThreshold = frameDurations[0];
    }

    public int duration() {
        return duration;
    }

    public int frames() {
        return frameDurations.length;
    }

    @Override
    public float textureU() {
        return 0;
    }

    @Override
    public float textureV() {
        return frameIndex * frameHeight;
    }

    @Override
    public int sourceWidth() {
        return frameWidth;
    }

    @Override
    public int sourceHeight() {
        return frameHeight * frameDurations.length;
    }

    @Override
    public int areaWidth() {
        return frameWidth;
    }

    @Override
    public int areaHeight() {
        return frameHeight;
    }

    @Override
    public @NotNull RenderPipeline pipeline() {
        return RenderPipelines.GUI_TEXTURED;
    }

    @Contract(value = " -> new", pure = true)
    public int @NonNull [] frameDelays() {
        return Arrays.copyOf(frameDurations, frameDurations.length);
    }

    @Override
    public void onTick(long deltaTime) {
        elapsedDuration += deltaTime;

        while (elapsedDuration >= durationThreshold) {
            elapsedDuration -= durationThreshold;

            if (++frameIndex >= frames()) {
                frameIndex = 0;
            }

            durationThreshold = frameDurations[frameIndex];
        }
    }
}
