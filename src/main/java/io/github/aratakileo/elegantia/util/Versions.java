package io.github.aratakileo.elegantia.util;

import org.jetbrains.annotations.NotNull;

import java.lang.module.ModuleDescriptor.Version;
import java.util.Optional;
import java.util.regex.Pattern;

public final class Versions {
    private Versions() {}

    private final static Pattern VERSION_KERNEL_PATTERN = Pattern.compile(
            "^([a-zA-Z]+((\\d+\\.)+\\d+)?[-_]?)?"
                    + "(?<kernel>(\\d+\\.)+\\d+(-(?:alpha|beta|a|b)(?![a-zA-Z])(\\.\\d+)?)?)"
    );

    public static @NotNull Optional<String> getVersionKernel(@NotNull String version) {
        final var matcher = VERSION_KERNEL_PATTERN.matcher(version);

        return matcher.find() ? Optional.of(matcher.group("kernel")) : Optional.empty();
    }

    public static @NotNull String getKernelVersionOrThrow(@NotNull String version) {
        return getVersionKernel(version).orElseThrow(
                () -> new InvalidVersionFormatException(version)
        );
    }

    public static int compareTo(@NotNull String leftVersion, @NotNull String rightVersion) {
        return Version.parse(leftVersion).compareTo(Version.parse(rightVersion));
    }

    public static boolean isGreaterThan(@NotNull String leftVersion, @NotNull String rightVersion) {
        return Version.parse(leftVersion).compareTo(Version.parse(rightVersion)) >= 1;
    }

    public static class InvalidVersionFormatException extends RuntimeException {
        public InvalidVersionFormatException(@NotNull String message) {
            super(message);
        }
    }
}
