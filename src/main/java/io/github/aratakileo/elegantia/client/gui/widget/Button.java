package io.github.aratakileo.elegantia.client.gui.widget;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.ElegantiaButtonDrawable;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Button extends AbstractButton {
    public Button(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);
        setBackgroundDrawable(new ElegantiaButtonDrawable());
    }

    @Override
    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        guiGraphics.renderScrollingText(getMessage(), bounds.copy().moveHorizontal(5, -5), 0xffffffff);
    }
}
