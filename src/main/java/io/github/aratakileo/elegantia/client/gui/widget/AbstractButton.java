package io.github.aratakileo.elegantia.client.gui.widget;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractButton extends AbstractWidget {
    private @Nullable OnClickListener onClickListener;

    public AbstractButton(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);
    }

    @Override
    public boolean onClick(boolean byUser) {
        if (onClickListener != null && onClickListener.onClick(this, byUser)) {
            if (byUser) ElGuiGraphics.playClickSound();
            return true;
        }

        return false;
    }

    public void setOnClickListener(@Nullable OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public interface OnClickListener {
        boolean onClick(@NotNull AbstractButton button, boolean byUser);
    }
}
