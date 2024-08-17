package io.github.aratakileo.elegantia.updatechecker;

import com.google.gson.JsonObject;
import io.github.aratakileo.elegantia.core.ModInfo;
import io.github.aratakileo.elegantia.core.Platform;
import io.github.aratakileo.elegantia.util.Versions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ModrinthResponse {
    private final static String NOT_FORMATTED_VERSION_PAGE_URL
            = "https://modrinth.com/mod/{project_id}/version/{version_id}";

    public final FailReason failReason;
    public final ModInfo mod;
    public final String projectId, minecraftVersion;
    public final Platform platform;

    private final @Nullable JsonObject versionMetadata;

    private ModrinthResponse(
            @NotNull FailReason failReason,
            @NotNull ModInfo mod,
            @NotNull String projectId,
            @NotNull String minecraftVersion,
            @Nullable JsonObject versionMetadata,
            @NotNull Platform platform
    ) {
        this.failReason = failReason;
        this.mod = mod;
        this.projectId = projectId;
        this.minecraftVersion = minecraftVersion;
        this.versionMetadata = versionMetadata;
        this.platform = platform;
    }

    public @NotNull Optional<String> getVersionName() {
        return versionMetadata == null
                ? Optional.empty()
                : Optional.of(versionMetadata.get("version_number").getAsString());
    }

    public @NotNull Optional<String> getVersionId() {
        return versionMetadata == null
                ? Optional.empty()
                : Optional.of(versionMetadata.get("id").getAsString());
    }

    public @NotNull Optional<String> getDownloadUrl() {
        return versionMetadata == null
                ? Optional.empty()
                : Optional.of(
                        versionMetadata.get("files")
                                .getAsJsonArray()
                                .get(0)
                                .getAsJsonObject()
                                .get("url")
                                .getAsString()
                );
    }

    private @NotNull Optional<String> getVersionPageUrl() {
        return versionMetadata == null
                ? Optional.empty()
                : Optional.of(NOT_FORMATTED_VERSION_PAGE_URL.replace("{project_id}", projectId)
                .replace("{version_id}", getVersionId().orElseThrow()));
    }

    public boolean isUpdateAvailable() {
        return versionMetadata != null && Versions.isGreaterThan(
                Versions.getVersionKernelOrThrow(getVersionName().orElseThrow()),
                Versions.getVersionKernelOrThrow(mod.getVersion())
        );
    }

    public boolean isSuccessful() {
        return versionMetadata != null;
    }

    public static @NotNull ModrinthResponse ofFailed(
            @NotNull ModrinthUpdateChecker modrinthUpdateChecker,
            @NotNull FailReason failReason
    ) {
        return of(modrinthUpdateChecker, failReason, null);
    }

    public static @NotNull ModrinthResponse ofSuccessful(
            @NotNull ModrinthUpdateChecker modrinthUpdateChecker,
            @NotNull JsonObject versionMetadata
    ) {
        return of(modrinthUpdateChecker, FailReason.NO_FAILS, versionMetadata);
    }

    private static @NotNull ModrinthResponse of(
            @NotNull ModrinthUpdateChecker modrinthUpdateChecker,
            @NotNull FailReason failReason,
            JsonObject versionMetadata
    ) {
        return new ModrinthResponse(
                failReason,
                modrinthUpdateChecker.mod,
                modrinthUpdateChecker.projectId,
                modrinthUpdateChecker.minecraftVersion,
                versionMetadata,
                modrinthUpdateChecker.platform
        );
    }
}
