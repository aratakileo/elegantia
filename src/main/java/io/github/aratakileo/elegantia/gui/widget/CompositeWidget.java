package io.github.aratakileo.elegantia.gui.widget;

import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.math.Rect2i;
import io.github.aratakileo.elegantia.util.Mouse;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * {@link CompositeWidget} automatically adds widgets marked with the {@link CompositePart} annotation
 * to the logic of its methods, such as: {@link AbstractWidget#render(GuiGraphics, int, int, float)},
 * {@link AbstractWidget#setX(int)}, {@link AbstractWidget#mouseClicked(double, double, Mouse.Button)}, etc.
 */
public abstract class CompositeWidget extends AbstractWidget {
    private final List<Object> compositeParts = new ArrayList<>();
    private final HashMap<String, Field> lateInitCompositeParts = new HashMap<>();

    public CompositeWidget(@NotNull Rect2i bounds, @Nullable Component message) {
        super(bounds, message);

        for (final var field: getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(CompositePart.class)) continue;

            final var lateInitAnchor = field.getAnnotation(CompositePart.class).lateInitAnchor();

            if (!lateInitAnchor.isBlank()) {
                lateInitCompositeParts.put(lateInitAnchor, field);
                continue;
            }

            addNewCompositePart(field);
        }
    }

    public CompositeWidget(@NotNull Rect2i bounds) {
        this(bounds, null);
    }

    protected void declareAsInited(@NotNull String lateInitAnchor) {
        if (!lateInitCompositeParts.containsKey(lateInitAnchor))
            throw new IllegalArgumentException("Late init anchor `%s` does not exist".formatted(lateInitAnchor));

        addNewCompositePart(lateInitCompositeParts.get(lateInitAnchor));

        lateInitCompositeParts.remove(lateInitAnchor);
    }

    private void addNewCompositePart(@NotNull Field field) {
        var fieldValue = (Object)null;

        try {
            fieldValue = field.get(this);
        } catch (Exception e) {
            Elegantia.LOGGER.error(
                    "Failed to add composite part from field `%s`".formatted(Classes.getFieldView(field))
            );
            return;
        }

        if (Objects.isNull(fieldValue))
            throw new NullPointerException("Value of field `%s` of a composite part cannot be null".formatted(
                            Classes.getFieldView(field)
            ));

        final var isLocalised = field.getAnnotation(CompositePart.class).isLocalised();

        if (fieldValue instanceof LayoutElement layoutElement) {
            if (isLocalised) {
                layoutElement.setX(getX() + layoutElement.getX());
                layoutElement.setY(getY() + layoutElement.getY());
            }
        }
        else if (fieldValue instanceof AbstractWidget abstractWidget) {
            if (isLocalised) {
                abstractWidget.setX(getX() + abstractWidget.getX());
                abstractWidget.setY(getY() + abstractWidget.getY());
            }
        } else if (!(fieldValue instanceof Renderable) || !(fieldValue instanceof GuiEventListener))
            throw new UnsupportedCompositePartException("in `%s`".formatted(Classes.getFieldView(field)));

        compositeParts.add(fieldValue);
    }

    @Override
    public void setX(int x) {
        final var valueDiff = x - getX();
        super.setX(x);

        for (final var widget: compositeParts) {
            if (widget instanceof AbstractWidget abstractWidget) {
                abstractWidget.setX(abstractWidget.getX() + valueDiff);
                continue;
            }

            final var layoutElement = (LayoutElement) widget;
            layoutElement.setX(layoutElement.getX() + valueDiff);
        }
    }

    @Override
    public void setY(int y) {
        final var valueDiff = y - getY();
        super.setY(y);

        for (final var unknownWidget: compositeParts) {
            if (unknownWidget instanceof AbstractWidget abstractWidget) {
                abstractWidget.setY(abstractWidget.getY() + valueDiff);
                continue;
            }

            final var layoutElement = (LayoutElement) unknownWidget;
            layoutElement.setY(layoutElement.getY() + valueDiff);
        }
    }

    @Override
    public void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float dt) {
        for (final var unknownWidget: compositeParts)
            ((Renderable) unknownWidget).render(guiGraphics, mouseX, mouseY, dt);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        for (final var unknownWidget: compositeParts)
            ((GuiEventListener) unknownWidget).mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, @NotNull Mouse.Button button) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).mouseClicked(mouseX, mouseY, button.ordinal())) return true;

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, @NotNull Mouse.Button button) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).mouseReleased(mouseX, mouseY, button.ordinal())) return true;

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(
            double mouseX,
            double mouseY,
            double deltaX,
            double deltaY,
            @NotNull Mouse.Button button
    ) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).mouseDragged(mouseX, mouseY, button.ordinal(), deltaX, deltaY))
                return true;

        return super.mouseDragged(mouseX, mouseY, deltaX, deltaY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (final var unknownWidget: compositeParts)
// 1.20-1.20.1
            if (((GuiEventListener) unknownWidget).mouseScrolled(mouseX, mouseY, verticalAmount))
// 1.20.2-1.20.4
//            if (((GuiEventListener) unknownWidget).mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount))
                return true;

        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).keyPressed(keyCode, scanCode, modifiers))
                return true;

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).keyReleased(keyCode, scanCode, modifiers))
                return true;

        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        for (final var unknownWidget: compositeParts)
            if (((GuiEventListener) unknownWidget).charTyped(chr, modifiers))
                return true;

        return false;
    }

    /**
     * Indicates that the widget in the selected field is part of the {@link CompositeWidget}
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface CompositePart {
        /**
         * Indicates that the position of the composite part widget was specified relative
         * to the {@link CompositeWidget} itself
         */
        boolean isLocalised() default true;

        /**
         * If this field is not empty then after the variable is initialized,
         * the function {@link CompositeWidget#declareAsInited(String)} must be called
         */
        @NotNull String lateInitAnchor() default "";
    }

    public static class UnsupportedCompositePartException extends RuntimeException {
        public UnsupportedCompositePartException(@NotNull String message) {
            super(message);
        }
    }
}
