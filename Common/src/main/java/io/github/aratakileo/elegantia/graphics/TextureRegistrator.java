package io.github.aratakileo.elegantia.graphics;

import com.mojang.blaze3d.platform.NativeImage;
import io.github.aratakileo.elegantia.framework.event.GuiTicker;
import io.github.aratakileo.elegantia.framework.resource.association.AssociatedResourceRegistry;
import io.github.aratakileo.elegantia.core.environment.Origin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

@ApiStatus.Experimental
public final class TextureRegistrator {
    private final Identifier id;
    private Supplier<String> labelFactory = null;
    private NativeImage image = null;
    private GifImage gifImage = null;

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

    public @NotNull TextureRegistrator label(@NotNull String label) {
        labelFactory = () -> label;
        return this;
    }

    public @NotNull TextureRegistrator label(@NotNull Supplier<String> labelFactory) {
        this.labelFactory = labelFactory;
        return this;
    }

    public @NotNull Identifier register() {
        if (labelFactory == null) throw new IllegalStateException("no label or label factory specified");

        if (gifImage != null) {
            final var texture = new DynamicTexture(labelFactory, gifImage.stitchFrames());
            final var association = gifImage.association();

            Minecraft.getInstance().getTextureManager().register(id, texture);
            AssociatedResourceRegistry.associate(id, association);
            GuiTicker.TICK.register(association);

            return id;
        }

        if (image == null) throw new IllegalStateException();

        final var texture = new DynamicTexture(labelFactory, image);
        Minecraft.getInstance().getTextureManager().register(id, texture);
        return id;
    }

    public static @NotNull TextureRegistrator create(@NotNull Identifier id) {
        return new TextureRegistrator(id);
    }

    public static @NotNull TextureRegistrator create(@NotNull Origin origin, @NotNull String path) {
        return new TextureRegistrator(origin.id(path));
    }
}
