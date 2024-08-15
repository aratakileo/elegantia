package io.github.aratakileo.elegantia.client.gui.widget;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.client.graphics.drawable.Drawable;
import io.github.aratakileo.elegantia.client.graphics.drawable.InteractionDrawable;
import io.github.aratakileo.elegantia.client.gui.tooltip.TooltipPositioner;
import io.github.aratakileo.elegantia.core.math.*;
import io.github.aratakileo.elegantia.client.MouseHandler;
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

import java.util.function.Supplier;

public abstract class AbstractWidget implements Renderable, GuiEventListener, NarratableEntry {
    private boolean isFocused = false, wasFocused = false;
    public boolean isEnabled = true, isVisible = true;

    protected boolean isHovered = false, wasHovered = false, wasHoveredBeforeRelease = false;
    protected @NotNull Supplier<@NotNull TooltipPositioner> tooltipPositionerGetter =
            () -> TooltipPositioner.getSimpleTooltipPositioner(
                    getBounds(),
                    !isHovered && isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard()
            );
    protected float alpha = 1.0F;
    protected @Nullable Drawable backgroundDrawable = null;

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

    public boolean isHovered() {
        return isHovered;
    }

    public boolean wasHovered() {
        return wasHovered;
    }

    public boolean wasHoveredBeforeRelease() {
        return wasHoveredBeforeRelease;
    }

    public int getWidth() {
        return bounds.width;
    }

    public void setWidth(int width) {
        bounds.setWidth(width);
    }

    public int getHeight() {
        return bounds.height;
    }

    public void setHeight(int height) {
        bounds.setHeight(height);
    }

    public int getX() {
        return bounds.x;
    }

    public int getY() {
        return bounds.y;
    }

    public void setY(int y) {
        bounds.setY(y);
    }

    public void setX(int x) {
        bounds.setX(x);
    }

    public @NotNull Vector2ic getPosition() {
        return bounds.getPosition();
    }

    public void setPosition(@NotNull Vector2iInterface position) {
        setX(position.x());
        setY(position.y());
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

    /**
     * @deprecated use {@link #render(ElGuiGraphics, Vector2ic, float)} instead
     */
    @Deprecated
    @Override
    public final void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        render(ElGuiGraphics.of(guiGraphics), new Vector2ic(mouseX, mouseY), dt);
    }

    public void render(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        if (!isVisible) return;

        wasFocused = isFocused() && Minecraft.getInstance().getLastInputType().isKeyboard();
        wasHovered = isHovered;
        isHovered = bounds.contains(mousePos);

        renderBackground(guiGraphics, mousePos, dt);
        renderForeground(guiGraphics, mousePos, dt);

        if (
                tooltip != null
                        && isVisible
                        && (isFocused() | isHovered)
                        && Minecraft.getInstance().screen != null
        )
            Minecraft.getInstance().screen.setTooltipForNextRenderPass(
                    tooltip,
                    tooltipPositionerGetter.get(),
                    isFocused()
            );
    }

    public void renderBackground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        if (backgroundDrawable == null) return;

        final var rectDrawer = guiGraphics.rect(bounds.copy());

        if (backgroundDrawable instanceof InteractionDrawable interactionDrawable) {
            interactionDrawable.setEnabled(isEnabled);
            interactionDrawable.setFocused(isFocused);
            interactionDrawable.setPressed(wasHoveredBeforeRelease);
            interactionDrawable.render(rectDrawer);
        } else backgroundDrawable.render(rectDrawer);
    }

    public void renderForeground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {}

    /**
     * @deprecated use {@link #mouseMoved(Vector2dc)} instead
     */
    @Deprecated
    @Override
    public final void mouseMoved(double mouseX, double mouseY) {
        mouseMoved(new Vector2dc(mouseX, mouseY));
    }

    public void mouseMoved(@NotNull Vector2dc pos) {}

    /**
     * @deprecated use {@link #mouseClicked(Vector2dc, MouseHandler.Button)} instead
     */
    @Deprecated
    @Override
    public final boolean mouseClicked(double mouseX, double mouseY, int button) {
        return mouseClicked(new Vector2dc(mouseX, mouseY), MouseHandler.Button.of(button));
    }

    public boolean mouseClicked(@NotNull Vector2dc mousePos, @NotNull MouseHandler.Button button) {
        wasHoveredBeforeRelease = false;

        return isEnabled
                && isVisible
                && button.isLeft()
                && bounds.contains(mousePos)
                && onMouseClick(mousePos);
    }

    public boolean onMouseClick(@NotNull Vector2dc mousePos) {
        wasHoveredBeforeRelease = true;

        return false;
    }

    /**
     * @deprecated use {@link #mouseReleased(Vector2dc, MouseHandler.Button)} instead
     */
    @Deprecated
    @Override
    public final boolean mouseReleased(double mouseX, double mouseY, int button) {
        return mouseReleased(new Vector2dc(mouseX, mouseY), MouseHandler.Button.of(button));
    }

    public boolean mouseReleased(@NotNull Vector2dc mousePos, @NotNull MouseHandler.Button button) {
        return isEnabled
                && isVisible
                && button.isLeft()
                && bounds.contains(mousePos)
                && onMouseRelease(mousePos);
    }

    public boolean onMouseRelease(@NotNull Vector2dc mousePos) {
        if (wasHoveredBeforeRelease) {
            wasHoveredBeforeRelease = false;
            return onClick(true);
        }

        return false;
    }

    /**
     * @deprecated use {@link #mouseDragged(Vector2dc, Vector2dc, MouseHandler.Button)} instead
     */
    @Deprecated
    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return mouseDragged(new Vector2dc(mouseX, mouseY), new Vector2dc(deltaX, deltaY), MouseHandler.Button.of(button));
    }

    public boolean mouseDragged(
            @NotNull Vector2dc mousePos,
            @NotNull Vector2dc delta,
            @NotNull MouseHandler.Button button
    ) {
        return isEnabled
                && isVisible
                && button.isLeft()
                && bounds.contains(mousePos)
                && onMouseDrag(mousePos, delta);
    }

    public boolean onMouseDrag(@NotNull Vector2dc mousePos, @NotNull Vector2dc delta) {
        return false;
    }

    /**
     * @deprecated use {@link #mouseScrolled(Vector2dc, Vector2dc)} instead
     */
    @Deprecated
    @Override
    public final boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        return mouseScrolled(new Vector2dc(mouseX, mouseY), new Vector2dc(horizontalAmount, verticalAmount));
    }

    public boolean mouseScrolled(@NotNull Vector2dc mousePos, @NotNull Vector2dc amount) {
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
        return isEnabled
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
    public ComponentPath nextFocusPath(@NotNull FocusNavigationEvent focusNavigationEvent) {
        return isEnabled && isVisible && !isFocused() ? ComponentPath.leaf(this) : null;
    }

    public final boolean isMouseOver(@NotNull Vector2dInterface mousePos) {
        return isMouseOver(mousePos.x(), mousePos.y());
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return isEnabled && bounds.contains(mouseX, mouseY);
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
        setX(bounds.x);
        setY(bounds.y);
        setWidth(bounds.width);
        setHeight(bounds.height);
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
        return isEnabled;
    }

    @Override
    public void updateNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        if (tooltip != null)
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
        this.message = message == null ? Component.empty() : message;
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

    public void setBackgroundDrawable(@Nullable Drawable backgroundDrawable) {
        this.backgroundDrawable = backgroundDrawable;
    }
}
