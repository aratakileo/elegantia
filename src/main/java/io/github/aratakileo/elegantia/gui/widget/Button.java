package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.util.graphics.GuiGraphicsUtil;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.util.graphics.RectDrawer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Button extends AbstractButton {
    public Button(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        new RectDrawer(guiGraphics, bounds)
                .draw(0xaa222222)
                .drawStroke(isHovered || isFocused() ? 0xffffffff : 0xaa000000, 1);

        GuiGraphicsUtil.renderScrollingText(
                guiGraphics,
                getMessage(),
                bounds.moveHorizontal(5, -5),
                0xffffffff
        );
    }
}
