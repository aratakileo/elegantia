package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.util.regex.Pattern;

public class Versions {
    private final static Pattern ONLY_NUMBER_PATTERN = Pattern.compile(
            "^([a-zA-Z]+((\\d+\\.)+\\d+)?[-_]?)?"
                    + "(?<num>(\\d+\\.)+\\d+(-(?:alpha|beta|a|b)(?![a-zA-Z])(\\.\\d+)?)?)"
    );

    public static @NotNull String getVersionNumber(@NotNull String version) {
        final var matcher = ONLY_NUMBER_PATTERN.matcher(version);

        if (matcher.find())
            return matcher.group("num");

        return "";
    }

    public static boolean isGreaterThan(@NotNull String leftVersion, @NotNull String rightVersion) {
        return Version.parse(rightVersion).compareTo(Version.parse(leftVersion)) >= 1;
    }
}
