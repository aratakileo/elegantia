package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.util.Mouse;
import io.github.aratakileo.elegantia.graphics.RectDrawer;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.NotNull;

public class VerticalScrollbar extends AbstractWidget {
    private final Rect2i thumbRect;
    private final int padding;

    private int maxProgress,
            segmentSize,
            progress = 0,
            scrollingThumbTopToTouchOffset = -1;

    public VerticalScrollbar(
            @NotNull Rect2i bounds,
            int maxProgress,
            int segmentSize,
            int padding
    ) {
        super(bounds);

        this.padding = padding;
        this.thumbRect = new Rect2i(0, 0, bounds.getWidth() - padding * 2, 0);

        setMaxProgress(maxProgress, segmentSize);
    }

    public boolean isScrolling() {
        return scrollingThumbTopToTouchOffset >= 0;
    }

    public int getProgress() {
        return progress;
    }

    protected @NotNull Rect2i getRenderableThumbRect() {
        return thumbRect.move(getX() + padding, getY() + padding);
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        setMaxProgress(maxProgress, segmentSize);
    }

    public void increaseMaxProgress(int increaseValue) {
        if (increaseValue == 0) return;

        setMaxProgress(maxProgress + increaseValue);
    }

    public void setMaxProgress(int maxProgress, int segmentSize) {
        this.maxProgress = maxProgress;
        this.segmentSize = segmentSize;

        thumbRect.setHeight(Math.min(
                getHeight() - 10 - padding * 2,
                Math.max(8, getHeight() - padding * 2 - maxProgress / segmentSize)
        ));

        setThumbY(padding + thumbRect.getY());
    }

    public void setProgress(int progress) {
        this.progress = Math.min(maxProgress, Math.max(progress, 0));
        this.thumbRect.setY((int) (
                (getHeight() - thumbRect.getHeight() - padding * 2)
                        * ((double) this.progress / (double) this.maxProgress)
        ));
    }

    protected void setScrollProgressByThumbY(int localY) {
        final var maxThumbTop = getHeight() - padding * 2 - thumbRect.getHeight();
        setThumbY(localY, maxThumbTop);
        progress = (int) (maxProgress * (double)thumbRect.getY() / (double)(maxThumbTop));
    }

    protected void setThumbY(int localY) {
        final var maxThumbTop = getHeight() - padding * 2 - thumbRect.getHeight();
        setThumbY(localY, maxThumbTop);
    }

    protected void setThumbY(int localY, int maxThumbTop) {
        thumbRect.setY(Math.min(Math.max(localY - padding, 0), maxThumbTop));
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        RectDrawer.with(guiGraphics, bounds)
                .draw(0xaa222222)
                .drawStroke(0xaa000000, padding);

        RectDrawer.with(guiGraphics, getRenderableThumbRect()).draw(
                getRenderableThumbRect().contains(mouseX, mouseY) ? 0xaacbcbcb : 0xaa6c757d
        );
    }

    @Override
    public boolean onMouseClick(double mouseX, double mouseY) {
        if (getRenderableThumbRect().contains(mouseX, mouseY)) {
            scrollingThumbTopToTouchOffset = (int) (mouseY - getRenderableThumbRect().getY());
            return true;
        }

        if (!isHovered) return false;

        setScrollProgressByThumbY((int) (mouseY - getY()));

        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        setProgress((int) (progress - verticalAmount));

        scrollingThumbTopToTouchOffset = -1;

        return true;
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            double deltaX,
            double deltaY,
            @NotNull Mouse.Button button
    ) {
        if (!isScrolling()) return false;

        setScrollProgressByThumbY((int) (mouseY - getY() - scrollingThumbTopToTouchOffset));

        return true;
    }

    @Override
    public boolean onMouseRelease(double mouseX, double mouseY) {
        scrollingThumbTopToTouchOffset = -1;

        return true;
    }
}
