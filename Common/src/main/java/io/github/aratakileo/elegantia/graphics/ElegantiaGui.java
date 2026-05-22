package io.github.aratakileo.elegantia.graphics;

import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.core.mixin.GuiGraphicsAccessors;
import io.github.aratakileo.elegantia.framework.resource.association.AssociatedResourceRegistry;
import io.github.aratakileo.elegantia.graphics.render.ResourceRenderer;
import io.github.aratakileo.elegantia.util.Exceptions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public final class ElegantiaGui extends GuiGraphics {
    private ElegantiaGui(
            @NotNull Minecraft minecraft,
            @NotNull GuiRenderState guiRenderState,
            int guiWidth,
            int guiHeight
    ) {
        super(minecraft, guiRenderState, guiWidth, guiHeight);
    }

    public void blitRenderer(@NotNull Identifier id, int x, int y, int width, int height) {
        final var association = AssociatedResourceRegistry.unwrap(id);

        if (association instanceof ResourceRenderer resourceRenderer) {
            resourceRenderer.render(this, id, x, y, width, height);
            return;
        }

        Exceptions.throwOrLog(Origin.from(id), IllegalArgumentException::new, id.toString());
    }

    public static @NotNull ElegantiaGui from(@NotNull GuiGraphics guiGraphics) {
        return new ElegantiaGui(
                ((GuiGraphicsAccessors)guiGraphics).elegantia$minecraft(),
                guiGraphics.guiRenderState,
                guiGraphics.guiWidth(),
                guiGraphics.guiHeight()
        );
    }
}
