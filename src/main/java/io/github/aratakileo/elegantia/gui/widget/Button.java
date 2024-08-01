package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.graphics.drawable.SimpleInteractionDrawable;
import io.github.aratakileo.elegantia.graphics.GuiGraphicsUtil;
import io.github.aratakileo.elegantia.util.math.Rect2i;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Button extends AbstractButton {
    public Button(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);
        setBackgroundDrawable(new SimpleInteractionDrawable());
    }

    @Override
    public void renderForeground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        GuiGraphicsUtil.renderScrollingText(
                guiGraphics,
                getMessage(),
                bounds.copy().moveHorizontal(5, -5),
                0xffffffff
        );
    }
}
