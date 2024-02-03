package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.gui.TooltipPositioner;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public abstract class AbstractWidget implements Renderable, GuiEventListener, NarratableEntry {
    private boolean isFocused = false, wasFocused = false;
    public boolean isActive = true, isVisible = true;

    protected boolean isHovered = false, wasHovered = false, wasHoveredBeforeRelease = false;
    protected @NotNull Supplier<@NotNull TooltipPositioner> tooltipPositionerGetter =
            () -> TooltipPositioner.getSimpleTooltipPositioner(
                    getBounds(),
                    !isHovered && isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard()
            );
    protected float alpha = 1.0F;
    protected final Rect2i bounds;

    private @NotNull Component message;
    private int tabOrderGroup;
    private @Nullable Tooltip tooltip;

    public AbstractWidget(@NotNull Rect2i bounds, @Nullable Component message) {
        this.bounds = bounds.copy();

        setMessage(message);
    }

    public AbstractWidget(@NotNull Rect2i bounds) {
        this(bounds, null);
    }

    public int getWidth() {
        return bounds.getWidth();
    }

    public void setWidth(int width) {
        bounds.setWidth(width);
    }

    public int getHeight() {
        return bounds.getHeight();
    }

    public void setHeight(int height) {
        bounds.setHeight(height);
    }

    public int getX() {
        return bounds.getX();
    }

    public int getY() {
        return bounds.getY();
    }

    public void setY(int y) {
        bounds.setY(y);
    }

    public void setX(int x) {
        bounds.setX(x);
    }

    public void setPosition(int x, int y) {
        setX(x);
        setY(y);
    }

    public void setLeftTop(int left, int top) {
        setPosition(left, top);
    }

    public void setRightBottom(int right, int bottom) {
        setRight(right);
        setBottom(bottom);
    }

    public int getRight() {
        return getX() + getWidth();
    }

    public void setRight(int right) {
        setX(right - getWidth());
    }

    public int getBottom() {
        return getY() + getHeight();
    }

    public void setBottom(int bottom) {
        setY(bottom - getHeight());
    }

    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        if (!isVisible) return;

        wasFocused = isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
        wasHovered = isHovered;
        isHovered = bounds.contains(mouseX, mouseY);

        renderWidget(guiGraphics, mouseX, mouseY, dt);

        if (
                Objects.nonNull(tooltip)
                        && isVisible
                        && (isFocused() | isHovered)
                        && Objects.nonNull(Minecraft.getInstance().screen)
        )
            Minecraft.getInstance().screen.setTooltipForNextRenderPass(
                    tooltip,
                    tooltipPositionerGetter.get(),
                    isFocused()
            );
    }

    public abstract void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt);

    @Override
    public void mouseMoved(double mouseX, double mouseY) {

    }

    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return mouseClicked(mouseX, mouseY, MouseButton.of(button));
    }

    public boolean mouseClicked(double mouseX, double mouseY, @NotNull MouseButton button) {
        wasHoveredBeforeRelease = false;

        return isActive
                && isVisible
                && button.isLeft()
                && bounds.contains(mouseX, mouseY)
                && onMouseClick(mouseX, mouseY);
    }

    public boolean onMouseClick(double mouseX, double mouseY) {
        wasHoveredBeforeRelease = true;

        return false;
    }

    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return mouseReleased(mouseX, mouseY, MouseButton.of(button));
    }

    public boolean mouseReleased(double mouseX, double mouseY, @NotNull MouseButton button) {
        return isActive
                && isVisible
                && button.isLeft()
                && bounds.contains(mouseX, mouseY)
                && onMouseRelease(mouseX, mouseY);
    }

    public boolean onMouseRelease(double mouseX, double mouseY) {
        if (wasHoveredBeforeRelease) {
            wasHoveredBeforeRelease = false;
            return onClick(true);
        }

        return false;
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return mouseDragged(mouseX, mouseY, deltaX, deltaY, MouseButton.of(button));
    }

    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            double deltaX,
            double deltaY,
            @NotNull MouseButton button
    ) {
        return isActive
                && isVisible
                && button.isLeft()
                && bounds.contains(mouseX, mouseY)
                && onMouseDrag(mouseX, mouseY, deltaX, deltaY);
    }

    public boolean onMouseDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        return false;
    }

    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double verticalAmount) {
        return mouseScrolled(mouseX, mouseY, 0, verticalAmount);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    public boolean onClick(boolean byUser) {
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return isActive
                && isFocused()
                && isVisible
                && (keyCode == 257 || keyCode == 32 || keyCode == 335)
                && onClick(true);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        return false;
    }

    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent focusNavigationEvent) {
        return isActive && isVisible && !isFocused() ? ComponentPath.leaf(this) : null;
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isActive && bounds.contains(mouseX, mouseY);
    }

    @Override
    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
    }

    @Override
    public boolean isFocused() {
        return isFocused;
    }

    public boolean wasFocused() {
        return wasFocused;
    }

    @Override
    public @NotNull ScreenRectangle getRectangle() {
        return GuiEventListener.super.getRectangle();
    }

    public void setBounds(@NotNull Rect2i bounds) {
        setX(bounds.getX());
        setY(bounds.getY());
        setWidth(bounds.getWidth());
        setHeight(bounds.getHeight());
    }

    public @NotNull Rect2i getBounds() {
        return bounds.copy();
    }

    @Override
    public @NotNull NarrationPriority narrationPriority() {
        return isFocused() ? NarrationPriority.FOCUSED : (
                isHovered ? NarrationPriority.HOVERED : NarrationPriority.NONE
        );
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationElementOutput) {
        if (Objects.nonNull(tooltip))
            tooltip.updateNarration(narrationElementOutput);
    }

    public void setTabOrderGroup(int tabOrderGroup) {
        this.tabOrderGroup = tabOrderGroup;
    }

    @Override
    public int getTabOrderGroup() {
        return tabOrderGroup;
    }

    public void setMessage(@Nullable Component message) {
        this.message = Objects.isNull(message) ? Component.empty() : message;
    }

    public @NotNull Component getMessage() {
        return message;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setTooltip(@NotNull String text) {
        setTooltip(Component.literal(text));
    }

    public void setTooltip(@NotNull Component text) {
        if (text.getString().isBlank()) {
            tooltip = null;
            return;
        }

        setTooltip(Tooltip.create(text));
    }

    public void setTooltip(@NotNull Tooltip tooltip) {
        this.tooltip = tooltip;
    }

    public @Nullable Tooltip getTooltip() {
        return tooltip;
    }

    public void disableTooltip() {
        tooltip = null;
    }

    public void setTooltipPositionerGetter(@NotNull Supplier<@NotNull TooltipPositioner> tooltipPositionerGetter) {
        this.tooltipPositionerGetter = tooltipPositionerGetter;
    }

    public enum MouseButton {
        LEFT,
        RIGHT,
        MIDDLE;

        public boolean isLeft() {
            return this == LEFT;
        }

        public boolean isRight() {
            return this == RIGHT;
        }

        public boolean isMiddle() {
            return this == MIDDLE;
        }

        public static @NotNull MouseButton of(int button) {
            return button == 1 ? RIGHT : (button == 2 ? MIDDLE : LEFT);
        }
    }
}
