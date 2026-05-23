package io.github.aratakileo.elegantia.graphics.render;

import io.github.aratakileo.elegantia.graphics.ElegantiaGui;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public interface ResourceRenderer {
    void render(@NotNull ElegantiaGui gui, @NotNull Identifier id, int x, int y, int width, int height);
}
