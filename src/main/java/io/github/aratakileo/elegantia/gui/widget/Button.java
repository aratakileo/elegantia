package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.util.GuiUtil;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

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

    public void setMessage(@NotNull Component message, int padding) {
        super.setMessage(message);

        final var oldWidth = getWidth();
        setWidth(Minecraft.getInstance().font.width(message) + padding * 2);

        if (getWidth() != oldWidth)
            setX(getX() - (getWidth() - oldWidth) / 2);
    }

    public static class Builder {
        private @Nullable Component message;
        private int padding = 5;
        private @NotNull Rect2i bounds = new Rect2i(0, 0, 0);
        private boolean hasLeftGravity = false, hasTopGravity = true;
        private @Nullable OnClickListener onClickListener;

        public @NotNull Builder setMessage(@Nullable Component message) {
            this.message = message;
            return this;
        }

        public @NotNull Builder setPadding(int padding) {
            this.padding = padding;
            return this;
        }

        public @NotNull Builder setBounds(@NotNull Rect2i rect2i) {
            bounds = rect2i.copy();

            return this;
        }

        public @NotNull Builder setHasLeftGravity(boolean hasLeftGravity) {
            this.hasLeftGravity = hasLeftGravity;

            return this;
        }

        public @NotNull Builder setHasTopGravity(boolean hasTopGravity) {
            this.hasTopGravity = hasTopGravity;

            return this;
        }

        public @NotNull Builder setOnClickListener(@Nullable OnClickListener onClickListener) {
            this.onClickListener = onClickListener;

            return this;
        }

        public @NotNull Button build() {
            if (padding != -1) {
                final var font = Minecraft.getInstance().font;
                final var oldHeight = bounds.getHeight();

                bounds.setHeight(Math.max(bounds.getHeight(), font.lineHeight + padding * 2));

                if (!hasTopGravity)
                    bounds.moveIpY((bounds.getHeight() - oldHeight) / -2);

                if (Objects.nonNull(message)) {
                    final var oldWidth = bounds.getWidth();
                    bounds.setWidth(font.width(message) + padding * 2);

                    if (!hasLeftGravity)
                        bounds.moveIpX((bounds.getWidth() - oldWidth) / -2);

                } else bounds.setWidth(Math.min(padding * 2, bounds.getWidth()));
            }

            return new Button(bounds, message) {{
                setOnClickListener(onClickListener);
            }};
        }
    }
}
