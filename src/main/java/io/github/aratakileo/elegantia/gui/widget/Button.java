package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.util.GuiUtil;
import io.github.aratakileo.elegantia.util.Rect2i;
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
        GuiUtil.drawRect(guiGraphics, getBounds(), 0xaa222222);

        if (isHovered || isFocused())
            GuiUtil.drawRectStroke(guiGraphics, getBounds(), 1, 0xffffffff);
        else GuiUtil.drawRectStroke(guiGraphics, getBounds(), 1, 0xaa000000);

        GuiUtil.renderScrollingText(
                guiGraphics,
                getMessage(),
                getBounds().moveIpHorizontal(5, -5),
                0xffffffff
        );
    }
}
