package io.github.aratakileo.elegantia.util;

import com.mojang.blaze3d.platform.Window;
import io.github.aratakileo.elegantia.util.math.Vector2dc;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface Mouse {
    private static @NotNull MouseHandler getMouseHandler() {
        return Minecraft.getInstance().mouseHandler;
    }

    private static @NotNull Window getWindow() {
        return Minecraft.getInstance().getWindow();
    }

    static double getX() {
        return getMouseHandler().xpos()
                * (double) getWindow().getGuiScaledWidth()
                / (double) getWindow().getScreenWidth();
    }

    static double getY() {
        return getMouseHandler().ypos()
                * (double) getWindow().getGuiScaledHeight()
                / (double) getWindow().getScreenHeight();
    }

    static @NotNull Vector2dc getPosition() {
        return new Vector2dc(getX(), getY());
    }

    static boolean isLeftPressed() {
        return getMouseHandler().isLeftPressed();
    }

    static boolean isRightPressed() {
        return getMouseHandler().isRightPressed();
    }

    static boolean isMiddlePressed() {
        return getMouseHandler().isMiddlePressed();
    }

    static @NotNull Optional<Button> getPressedButton() {
        if (getMouseHandler().isLeftPressed())
            return Optional.of(Button.LEFT);

        if (getMouseHandler().isRightPressed())
            return Optional.of(Button.RIGHT);

        if (getMouseHandler().isMiddlePressed())
            return Optional.of(Button.MIDDLE);

        return Optional.empty();
    }

    enum Button {
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

        public static @NotNull Mouse.Button of(int button) {
            return button == 1 ? RIGHT : (button == 2 ? MIDDLE : LEFT);
        }
    }
}
