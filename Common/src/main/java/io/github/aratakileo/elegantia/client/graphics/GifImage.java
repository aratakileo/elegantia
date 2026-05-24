package io.github.aratakileo.elegantia.client.graphics;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.client.graphics.render.GifRenderer;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

@ApiStatus.Experimental
public final class GifImage implements AutoCloseable {
    private final int width;
    private final int height;
    private final int frames;
    private final int[] frameDurations;

    private ByteBuffer gifByteBuffer;

    public GifImage(int width, int height, int frames, @NotNull ByteBuffer gifByteBuffer, int[] frameDurations) {
        this.width = width;
        this.height = height;
        this.frames = frames;
        this.gifByteBuffer = gifByteBuffer;
        this.frameDurations = frameDurations;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public int frames() {
        return frameDurations.length;
    }

    public int fullDurationMs() {
        return Arrays.stream(frameDurations).sum();
    }

    public int[] frameDurationsMs() {
        return Arrays.copyOf(frameDurations, frameDurations.length);
    }

    public @NotNull GifRenderer renderer() {
        return new GifRenderer(width, height, fullDurationMs(), frameDurations);
    }

    public @NotNull NativeImage stitchFrames() {
        final var imageRoll = new NativeImage(NativeImage.Format.RGBA, width, height * frames, false);
        final var frameSize = width * height * 4;

        for (var frame = 0; frame < frames; frame++) {
            gifByteBuffer.position(frame * frameSize);

            for (var y = 0; y < height; y++) {
                int globalY = (frame * height) + y;

                for (var x = 0; x < width; x++) {
                    final var r = gifByteBuffer.get() & 0xFF;
                    final var g = gifByteBuffer.get() & 0xFF;
                    final var b = gifByteBuffer.get() & 0xFF;
                    final var a = gifByteBuffer.get() & 0xFF;
                    final var abgr = (a << 24) | (b << 16) | (g << 8) | r;

                    imageRoll.setPixelABGR(x, globalY, abgr);
                }
            }
        }

        gifByteBuffer.position(0);
        return imageRoll;
    }

    public int processFrames(@NotNull FrameConsumer consumer) {
        final var frameSize = width * height * 4;
        var elapsedDuration = 0;

        for (var frameIndex = 0; frameIndex < frames; frameIndex++) {
            final var frame = new NativeImage(NativeImage.Format.RGBA, width, height, false);
            gifByteBuffer.position(frameIndex * frameSize);

            for (var y = 0; y < height; y++) {
                for (var x = 0; x < width; x++) {
                    final var r = gifByteBuffer.get() & 0xFF;
                    final var g = gifByteBuffer.get() & 0xFF;
                    final var b = gifByteBuffer.get() & 0xFF;
                    final var a = gifByteBuffer.get() & 0xFF;
                    final var abgr = (a << 24) | (b << 16) | (g << 8) | r;

                    frame.setPixelABGR(x, y, abgr);
                }
            }

            consumer.consume(frame, frameIndex, frameDurations[frameIndex], elapsedDuration);
            frame.close();

            elapsedDuration += frameDurations[frameIndex];
        }

        gifByteBuffer.position(0);
        return elapsedDuration;
    }

    public static @NotNull Result<GifImage> read(@NotNull InputStream inputStream) {
        final var result = Result.fromFactory(() -> read(inputStream.readAllBytes()))
                .flatMap(res -> res);

        IOUtils.closeQuietly(inputStream);
        return result;
    }

    public static @NotNull Result<GifImage> read(byte[] fileData) {
        final var byteBuffer = MemoryUtil.memAlloc(fileData.length);

        return Result.fromFactory(() -> {
            byteBuffer.put(fileData);
            byteBuffer.position(0);

            try (final MemoryStack memoryStack = MemoryStack.stackPush()) {
                final var delayBuffer = memoryStack.mallocPointer(1);
                final var widthBuffer = memoryStack.mallocInt(1);
                final var heightBuffer = memoryStack.mallocInt(1);
                final var frameBuffer = memoryStack.mallocInt(1);
                final var channelBuffer = memoryStack.mallocInt(1);

                final var gifByteBuffer = STBImage.stbi_load_gif_from_memory(
                        byteBuffer,
                        delayBuffer,
                        widthBuffer,
                        heightBuffer,
                        frameBuffer,
                        channelBuffer,
                        4
                );

                if (gifByteBuffer == null)
                    throw new IOException("could not load gif image: " + STBImage.stbi_failure_reason());

                final var width = widthBuffer.get();
                final var height = heightBuffer.get();
                final var frames = frameBuffer.get();

                final var delaysIntBuffer = delayBuffer.getIntBuffer(frames);
                final var delays = new int[frames];
                delaysIntBuffer.get(delays);

                // centiseconds to milliseconds
                for (var i = 0; i < delays.length; i++) delays[i] *= 10;

                return new GifImage(width, height, frames, gifByteBuffer, delays);
            }
        }).runFinally(() -> MemoryUtil.memFree(byteBuffer));
    }

    @Override
    public void close() {
        if (gifByteBuffer == null) return;

        STBImage.stbi_image_free(gifByteBuffer);
        gifByteBuffer = null;
    }

    @FunctionalInterface
    public interface FrameConsumer {
        void consume(@NotNull NativeImage frame, int index, int durationMs, int elapsedDurationMs);
    }
}
