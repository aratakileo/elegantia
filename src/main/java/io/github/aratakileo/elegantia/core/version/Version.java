package io.github.aratakileo.elegantia.core.version;

import io.github.aratakileo.elegantia.util.type.InitOnGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor;
import java.util.Optional;

public class Version extends AbstractVersion<Version> {
    private final @NotNull InitOnGet<KernelVersion> kernelGetter = InitOnGet.buildOptional(
            versionName,
            KernelVersion::of
    );

    private Version(@Nullable ModuleDescriptor.Version _version, @NotNull String versionName) {
        super(_version, versionName);
    }

    public @NotNull Optional<KernelVersion> kernel() {
        return kernelGetter.getOptional();
    }

    public static @NotNull Version parse(@NotNull String version) {
        try {
            return new Version(ModuleDescriptor.Version.parse(version), version);
        } catch (Exception e) {
            return new Version(null, version);
        }
    }
}
