package io.github.aratakileo.elegantia.client.gui.widget;

import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.client.graphics.ElGuiGraphics;
import io.github.aratakileo.elegantia.core.math.Vector2dc;
import io.github.aratakileo.elegantia.core.math.Vector2ic;
import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.core.math.Rect2i;
import io.github.aratakileo.elegantia.client.MouseHandler;
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

/**
 * {@link CompositeWidget} automatically adds widgets marked with the {@link CompositePart} annotation
 * to the logic of its methods, such as: {@link #render(ElGuiGraphics, Vector2ic, float)},
 * {@link #setX(int)}, {@link #mouseClicked(Vector2dc, MouseHandler.Button)}, etc.
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

        if (fieldValue == null)
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
        else if (fieldValue instanceof AbstractWidget elWidget) {
            if (isLocalised) {
                elWidget.setX(getX() + elWidget.getX());
                elWidget.setY(getY() + elWidget.getY());
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
            if (widget instanceof AbstractWidget elWidget) {
                elWidget.setX(elWidget.getX() + valueDiff);
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
            if (unknownWidget instanceof AbstractWidget elWidget) {
                elWidget.setY(elWidget.getY() + valueDiff);
                continue;
            }

            final var layoutElement = (LayoutElement) unknownWidget;
            layoutElement.setY(layoutElement.getY() + valueDiff);
        }
    }

    @Override
    public void renderBackground(@NotNull ElGuiGraphics guiGraphics, @NotNull Vector2ic mousePos, float dt) {
        super.renderBackground(guiGraphics, mousePos, dt);

        for (final var unknownWidget: compositeParts)
            if (unknownWidget instanceof AbstractWidget elWidget)
                elWidget.render(guiGraphics, mousePos, dt);
            else ((Renderable) unknownWidget).render(guiGraphics, mousePos.x, mousePos.y, dt);
    }

    @Override
    public void mouseMoved(@NotNull Vector2dc pos) {
        for (final var unknownWidget: compositeParts)
            if (unknownWidget instanceof AbstractWidget elWidget)
                elWidget.mouseMoved(pos);
            else ((GuiEventListener) unknownWidget).mouseMoved(pos.x, pos.y);
    }

    @Override
    public boolean mouseClicked(@NotNull Vector2dc mousePos, @NotNull MouseHandler.Button button) {
        for (final var unknownWidget: compositeParts) {
            if (unknownWidget instanceof AbstractWidget elWidget) {
                if (elWidget.mouseClicked(mousePos, button))
                    return true;
                
                continue;
            }
            
            if (((GuiEventListener) unknownWidget).mouseClicked(mousePos.x, mousePos.y, button.ordinal()))
                return true;
        }
        
        return super.mouseClicked(mousePos, button);
    }

    @Override
    public boolean mouseReleased(@NotNull Vector2dc mousePos, @NotNull MouseHandler.Button button) {
        for (final var unknownWidget: compositeParts) {
            if (unknownWidget instanceof AbstractWidget elWidget) {
                if (elWidget.mouseReleased(mousePos, button))
                    return true;

                continue;
            }

            if (((GuiEventListener) unknownWidget).mouseReleased(mousePos.x, mousePos.y, button.ordinal()))
                return true;
        }
        
        return super.mouseReleased(mousePos, button);
    }

    @Override
    public boolean mouseDragged(
            @NotNull Vector2dc mousePos, 
            @NotNull Vector2dc delta, 
            @NotNull MouseHandler.Button button
    ) {
        for (final var unknownWidget: compositeParts) {
            if (unknownWidget instanceof AbstractWidget elWidget) {
                if (elWidget.mouseDragged(mousePos, delta, button))
                    return true;

                continue;
            }

            if (((GuiEventListener) unknownWidget).mouseDragged(
                    mousePos.x, 
                    mousePos.y,
                    button.ordinal(),
                    delta.x,
                    delta.y
            ))
                return true;
        }
        
        return super.mouseDragged(mousePos, delta, button);
    }

    @Override
    public boolean mouseScrolled(@NotNull Vector2dc mousePos, @NotNull Vector2dc amount) {
        for (final var unknownWidget: compositeParts) {
            if (unknownWidget instanceof AbstractWidget elWidget) {
                if (elWidget.mouseScrolled(mousePos, amount))
                    return true;

                continue;
            }

            if (((GuiEventListener) unknownWidget).mouseScrolled(mousePos.x, mousePos.y, amount.x, amount.y))
                return true;
        }
        
        return super.mouseScrolled(mousePos, amount);
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
