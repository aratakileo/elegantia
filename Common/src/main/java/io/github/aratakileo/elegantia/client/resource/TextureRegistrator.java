package io.github.aratakileo.elegantia.client.resource;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.aratakileo.elegantia.client.event.GuiTicker;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.client.graphics.GifImage;
import io.github.aratakileo.elegantia.client.graphics.render.ResourceRenderer;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@ApiStatus.Experimental
public final class TextureRegistrator {
    private final Identifier id;
    private Supplier<String> labelFactory = null;
    private NativeImage image = null;
    private GifImage gifImage = null;
    private ResourceRenderer renderer = null;

    private TextureRegistrator(@NotNull Identifier id) {
        this.id = id;
    }

    public @NotNull TextureRegistrator source(@NotNull NativeImage image) {
        this.image = image;
        this.gifImage = null;
        return this;
    }

    public @NotNull TextureRegistrator source(@NotNull GifImage image) {
        this.image = null;
        this.gifImage = image;
        return this;
    }

    public @NotNull TextureRegistrator renderer(@NotNull ResourceRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    public @NotNull TextureRegistrator label(@NotNull String label) {
        labelFactory = () -> label;
        return this;
    }

    public @NotNull TextureRegistrator label(@NotNull Supplier<String> labelFactory) {
        this.labelFactory = labelFactory;
        return this;
    }

    public @NotNull Identifier register() {
        if (Exceptions.throwOrLogIf(
                labelFactory == null,
                id.getNamespace(),
                IllegalStateException::new,
                "no label or label factory present"
        )) return id;

        if (gifImage != null) {
            reg(
                    new DynamicTexture(labelFactory, gifImage.stitchFrames()),
                    renderer == null ? gifImage.renderer() : renderer
            );

            return id;
        }

        if (Exceptions.throwOrLogIf(
                image == null,
                id.getNamespace(),
                IllegalStateException::new,
                "no images present"
        )) return id;

        reg(new DynamicTexture(labelFactory, image), renderer);
        return id;
    }

    private void reg(@NotNull DynamicTexture texture, @Nullable ResourceRenderer renderer) {
        Minecraft.getInstance().getTextureManager().register(id, texture);

        if (renderer == null) return;

        if (AssociatedResourceRegistry.has(id)) {
            Origin.from(id).logger().warn(
                    "The texture with id `{}` was overwritten with a new source, but its entry in `{}` " +
                            "was NOT updated because this entry can be presented only once. " +
                            "If the new texture has different dimensions or frame counts, rendering may be broken. " +
                            "If the person reading this message is a player " +
                            "and you are experiencing issues with the texture rendering mentioned earlier, " +
                            "please contact the mod developer. Otherwise, you can safely ignore this message.",
                    id,
                    AssociatedResourceRegistry.class.getSimpleName()
            );
            return;
        }

        AssociatedResourceRegistry.associate(id, renderer);

        if (renderer instanceof GuiTicker.Listener tickableRenderer)
            GuiTicker.TICK.register(tickableRenderer);
    }

    public static @NotNull TextureRegistrator create(@NotNull Identifier id) {
        return new TextureRegistrator(id);
    }

    public static @NotNull TextureRegistrator create(@NotNull Origin origin, @NotNull String path) {
        return new TextureRegistrator(origin.id(path));
    }
}
