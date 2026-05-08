package io.github.aratakileo.elegantia.core.version;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.module.ModuleDescriptor;

public abstract class AbstractVersion<V extends AbstractVersion<?>> {
    final protected @Nullable ModuleDescriptor.Version _version;
    final protected @NotNull String versionName;

    protected AbstractVersion(@Nullable ModuleDescriptor.Version _version, @NotNull String versionName) {
        this._version = _version;
        this.versionName = versionName;
    }

    public boolean equals(@NotNull V version) {
        return compareTo(version) == 0 && canBeCompared(version);
    }

    public boolean equals(@NotNull String version) {
        return compareTo(version) == 0 && _version != null;
    }

    public boolean isLess(@NotNull V version) {
        return compareTo(version) < 0;
    }

    public boolean isLess(@NotNull String version) {
        return compareTo(version) < 0;
    }

    public boolean isGreater(@NotNull V version) {
        return compareTo(version) >= 1;
    }

    public boolean isGreater(@NotNull String version) {
        return compareTo(version) >= 1;
    }

    public boolean canBeCompared(@NotNull V version) {
        return _version != null && version._version != null;
    }

    public boolean isValid() {
        return _version != null;
    }

    public int compareTo(@NotNull V version) {
        if (_version == null || version._version == null)
            return 0;

        return _version.compareTo(version._version);
    }

    public int compareTo(@NotNull String version) {
        if (_version == null)
            return 0;

        try {
            return _version.compareTo(ModuleDescriptor.Version.parse(version));
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public @NotNull String toString() {
        return versionName;
    }
}
