package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Optional;
import java.util.regex.Pattern;

public class Versions {
    private Versions() {}

    private final static Pattern ONLY_NUMBER_PATTERN = Pattern.compile(
            "^([a-zA-Z]+((\\d+\\.)+\\d+)?[-_]?)?"
                    + "(?<kernel>(\\d+\\.)+\\d+(-(?:alpha|beta|a|b)(?![a-zA-Z])(\\.\\d+)?)?)"
    );

    public static @NotNull Optional<String> getVersionKernel(@NotNull String version) {
        final var matcher = ONLY_NUMBER_PATTERN.matcher(version);

        return matcher.find() ? Optional.of(matcher.group("kernel")) : Optional.empty();
    }

    public static boolean isGreaterThan(@NotNull String leftVersion, @NotNull String rightVersion) {
        return Version.parse(rightVersion).compareTo(Version.parse(leftVersion)) >= 1;
    }
}
