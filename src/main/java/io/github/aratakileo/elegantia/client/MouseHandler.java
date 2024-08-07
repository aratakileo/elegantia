package io.github.aratakileo.elegantia.client;

import com.mojang.blaze3d.platform.Window;
import io.github.aratakileo.elegantia.core.math.Vector2dc;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class MouseHandler {
    static @NotNull net.minecraft.client.MouseHandler getMouseHandler() {
        return Minecraft.getInstance().mouseHandler;
    }

    private static @NotNull Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }

    public static double getX() {
        return getMouseHandler().xpos()
                * (double) getWindow().getGuiScaledWidth()
                / (double) getWindow().getScreenWidth();
    }

    public static double getY() {
        return getMouseHandler().ypos()
                * (double) getWindow().getGuiScaledHeight()
                / (double) getWindow().getScreenHeight();
    }

    public static @NotNull Vector2dc getPosition() {
        return new Vector2dc(getX(), getY());
    }

    public static boolean isLeftPressed() {
        return getMouseHandler().isLeftPressed();
    }

    public static boolean isRightPressed() {
        return getMouseHandler().isRightPressed();
    }

    public static boolean isMiddlePressed() {
        return getMouseHandler().isMiddlePressed();
    }

    public static @NotNull Optional<Button> getPressedButton() {
        if (getMouseHandler().isLeftPressed())
            return Optional.of(Button.LEFT);

        if (getMouseHandler().isRightPressed())
            return Optional.of(Button.RIGHT);

        if (getMouseHandler().isMiddlePressed())
            return Optional.of(Button.MIDDLE);

        return Optional.empty();
    }

    public enum Button {
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

        public static @NotNull MouseHandler.Button of(int button) {
            return button == 1 ? RIGHT : (button == 2 ? MIDDLE : LEFT);
        }
    }
}
