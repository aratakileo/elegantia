package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.graphics.GifImage;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.concurrent.CompletableFuture;

public final class Resources {
    private Resources() {}

    public static @NotNull Result<GifImage> readGif(@NotNull Identifier id) {
        return readStream(id).flatMap(GifImage::read);
    }

    public static @NotNull CompletableFuture<Result<GifImage>> readGifAsync(@NotNull Identifier id) {
        return CompletableFuture.supplyAsync(() -> readStream(id).flatMap(GifImage::read), Util.ioPool());
    }

    public static @NotNull Result<InputStream> readStream(@NotNull Identifier id) {
        final var optionalResource = Minecraft.getInstance()
                .getResourceManager()
                .getResource(id);

        return optionalResource.map(resource -> Result.fromFactory(resource::open))
                .orElseGet(() -> Result.fromError(IllegalArgumentException::new, id.toString()));
    }

    public static @NotNull CompletableFuture<Result<InputStream>> readStreamAsync(@NotNull Identifier id) {
        return CompletableFuture.supplyAsync(() -> readStream(id), Util.ioPool());
    }
}
