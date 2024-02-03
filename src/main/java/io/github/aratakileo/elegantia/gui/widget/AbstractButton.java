package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.util.GuiUtil;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Function;

public abstract class AbstractButton extends AbstractWidget {
    private @Nullable OnClickListener onClickListener;

    public AbstractButton(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);
    }

    @Override
    public boolean onClick(boolean byUser) {
        if (Objects.nonNull(onClickListener) && onClickListener.onClick(this, byUser)) {
            if (byUser) GuiUtil.playClickSound();
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
