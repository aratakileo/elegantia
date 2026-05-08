package io.github.aratakileo.elegantia.core.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor;
import java.util.Optional;
import java.util.regex.Pattern;

public class KernelVersion extends AbstractVersion<KernelVersion> {
    private final static Pattern VERSION_KERNEL_PATTERN = Pattern.compile(
            "^([a-zA-Z]+((\\d+\\.)+\\d+)?[-_]?)?"
                    + "(?<kernel>(\\d+\\.)+\\d+(-(?:alpha|beta|a|b)(?![a-zA-Z])(\\.\\d+)?)?)"
    );

    protected KernelVersion(@Nullable ModuleDescriptor.Version _version, @NotNull String versionName) {
        super(_version, versionName);
    }

    public boolean equals(@NotNull Version version) {
        return compareTo(version) == 0 && canBeCompared(version);
    }

    public boolean isLess(@NotNull Version version) {
        return compareTo(version) < 0;
    }

    public boolean isGreater(@NotNull Version version) {
        return compareTo(version) >= 1;
    }

    public boolean canBeCompared(@NotNull Version version) {
        return _version != null && version.kernel().map(kernel -> kernel._version != null).orElse(false);
    }

    public int compareTo(@NotNull Version version) {
        return version.kernel().map(kernelVersion -> -kernelVersion.compareTo(this)).orElseThrow();
    }

    public static @NotNull Optional<KernelVersion> of(@NotNull String version) {
        final var matcher = VERSION_KERNEL_PATTERN.matcher(version);

        return matcher.find() ? Optional.of(parse(matcher.group("kernel"))) : Optional.empty();
    }

    public static @NotNull KernelVersion parse(@NotNull String kernelVersion) {
        try {
            return new KernelVersion(ModuleDescriptor.Version.parse(kernelVersion), kernelVersion);
        } catch (Exception e) {
            return new KernelVersion(null, kernelVersion);
        }
    }
}
