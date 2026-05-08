package io.github.aratakileo.elegantia.client.graphics;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

public class Color {
    public final int r, g, b, a;

    protected Color(int r, int g, int b, int a) {
        Preconditions.checkArgument(r >= 0 && r <= 0xff, "red value must be in range from 0 to 255");
        Preconditions.checkArgument(g >= 0 && g <= 0xff, "green value must be in range from 0 to 255");
        Preconditions.checkArgument(b >= 0 && b <= 0xff, "blue value must be in range from 0 to 255");
        Preconditions.checkArgument(a >= 0 && a <= 0xff, "alpha value must be in range from 0 to 255");

        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public static @NotNull Color rgb(int r, int g, int b) {
        return new Color(0xff, r, g, b);
    }

    public static @NotNull Color hex(int color) {
        return rgb((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff);
    }

    public static @NotNull Color rgba(int r, int g, int b, int a) {
        return new Color(r, g, b, a);
    }

    public static @NotNull Color ahex(int color) {
        return new Color((color >> 16) & 0xff, (color >> 8) & 0xff, color & 0xff, (color >> 24) & 0xff);
    }

    public static @NotNull Color of(@NotNull java.awt.Color color) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
    }
}
