package io.github.aratakileo.elegantia.core;

import io.github.aratakileo.elegantia.client.graphics.drawable.TextureDrawable;
import io.github.aratakileo.elegantia.core.math.Vector2fInterface;
import io.github.aratakileo.elegantia.util.type.InitOnGet;
import org.jetbrains.annotations.NotNull;

public class TextureProvider {
    private final Namespace namespace;

    public TextureProvider(@NotNull Namespace namespace) {
        this.namespace = namespace;
    }

    public @NotNull InitOnGet<TextureDrawable> define(@NotNull String path) {
        return TextureDrawable.safeAutoSize(namespace.getLocation(path));
    }

    public @NotNull InitOnGet<TextureDrawable> define(@NotNull String path, @NotNull Vector2fInterface uv) {
        return TextureDrawable.safeAutoSize(namespace.getLocation(path), uv);
    }

    public @NotNull InitOnGet<TextureDrawable> define(@NotNull String path, float u, float v) {
        return TextureDrawable.safeAutoSize(namespace.getLocation(path), u, v);
    }

    public @NotNull InitOnGet<TextureDrawable> defineGui(@NotNull String path) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/" + path));
    }

    public @NotNull InitOnGet<TextureDrawable> defineGui(@NotNull String path, @NotNull Vector2fInterface uv) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/" + path), uv);
    }

    public @NotNull InitOnGet<TextureDrawable> defineGui(@NotNull String path, float u, float v) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/" + path), u, v);
    }

    public @NotNull InitOnGet<TextureDrawable> defineGuiIcon(@NotNull String path) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/" + path));
    }

    public @NotNull InitOnGet<TextureDrawable> defineGuiIcon(@NotNull String path, @NotNull Vector2fInterface uv) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/" + path), uv);
    }

    public @NotNull InitOnGet<TextureDrawable> defineGuiIcon(@NotNull String path, float u, float v) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/" + path), u, v);
    }

    public @NotNull InitOnGet<TextureDrawable> defineSlotIcon(@NotNull String path) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/slot/" + path));
    }

    public @NotNull InitOnGet<TextureDrawable> defineSlotIcon(@NotNull String path, @NotNull Vector2fInterface uv) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/slot/" + path), uv);
    }

    public @NotNull InitOnGet<TextureDrawable> defineSlotIcon(@NotNull String path, float u, float v) {
        return TextureDrawable.safeAutoSize(namespace.getLocation("textures/gui/icon/slot/" + path), u, v);
    }
}
