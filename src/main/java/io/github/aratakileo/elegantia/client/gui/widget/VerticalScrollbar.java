package io.github.aratakileo.elegantia.client.gui.widget;

import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.client.MouseProvider;
import io.github.aratakileo.elegantia.core.math.Vector2dc;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
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
        this.thumbRect = Rect2i.zeroPosition(bounds.width - padding * 2, 0);

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

        setThumbY(padding + thumbRect.y);
    }

    public void setProgress(int progress) {
        this.progress = Math.min(maxProgress, Math.max(progress, 0));
        this.thumbRect.setY((int) (
                (getHeight() - thumbRect.height - padding * 2)
                        * ((double) this.progress / (double) this.maxProgress)
        ));
    }

    protected void setScrollProgressByThumbY(int localY) {
        final var maxThumbTop = getHeight() - padding * 2 - thumbRect.height;
        setThumbY(localY, maxThumbTop);
        progress = (int) (maxProgress * (double)thumbRect.y / (double)(maxThumbTop));
    }

    protected void setThumbY(int localY) {
        final var maxThumbTop = getHeight() - padding * 2 - thumbRect.height;
        setThumbY(localY, maxThumbTop);
    }

    protected void setThumbY(int localY, int maxThumbTop) {
        thumbRect.setY(Math.min(Math.max(localY - padding, 0), maxThumbTop));
    }

    @Override
    public void renderBackground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        guiGraphics.rect(bounds).draw(0xaa222222).drawStroke(0xaa000000, padding);
        guiGraphics.rect(getRenderableThumbRect()).draw(
                getRenderableThumbRect().contains(mousePos) ? 0xaacbcbcb : 0xaa6c757d
        );
    }

    @Override
    public boolean onMouseClick(@NotNull Vector2dc mousePos) {
        if (getRenderableThumbRect().contains(mousePos)) {
            scrollingThumbTopToTouchOffset = (int) (mousePos.y - getRenderableThumbRect().y);
            return true;
        }

        if (!isHovered) return false;

        setScrollProgressByThumbY((int) (mousePos.y - getY()));

        return true;
    }

    @Override
    public boolean mouseScrolled(@NotNull Vector2dc mousePos, @NotNull Vector2dc amount) {
        setProgress((int) (progress - amount.y));

        scrollingThumbTopToTouchOffset = -1;

        return true;
    }

    @Override
    public boolean mouseDragged(
            @NotNull Vector2dc mousePos,
            @NotNull Vector2dc delta,
            @NotNull MouseProvider.Button button
    ) {
        if (!isScrolling()) return false;

        setScrollProgressByThumbY((int) (mousePos.y - getY() - scrollingThumbTopToTouchOffset));

        return true;
    }

    @Override
    public boolean onMouseRelease(@NotNull Vector2dc mousePos) {
        scrollingThumbTopToTouchOffset = -1;

        return true;
    }
}
