package io.github.aratakileo.elegantia.updatechecker;

import com.google.gson.JsonObject;
import io.github.aratakileo.elegantia.core.ModInfo;
import io.github.aratakileo.elegantia.core.Platform;
import io.github.aratakileo.elegantia.core.version.Version;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ModrinthResponse {
    private final static String NOT_FORMATTED_VERSION_PAGE_URL
            = "https://modrinth.com/mod/{project_id}/version/{version_id}";

    public final @NotNull FailReason failReason;
    public final @NotNull ModInfo mod;
    public final @NotNull String projectId, minecraftVersion;
    public final @NotNull Platform platform;

    private final @Nullable JsonObject versionMetadata;
    private final @Nullable Version version;

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
        this.version = Optional.ofNullable(versionMetadata)
                .map(data -> data.get("version_number").getAsString())
                .map(Version::parse)
                .orElse(null);
        this.platform = platform;
    }

    public @NotNull Optional<Version> version() {
        return Optional.ofNullable(version);
    }

    public @NotNull Optional<String> versionId() {
        return Optional.ofNullable(versionMetadata).map(data -> data.get("id").getAsString());
    }

    public @NotNull Optional<String> downloadUrl() {
        return Optional.ofNullable(versionMetadata).map(
                data -> data.get("files")
                        .getAsJsonArray()
                        .get(0)
                        .getAsJsonObject()
                        .get("url")
                        .getAsString()
        );
    }

    private @NotNull Optional<String> versionPageUrl() {
        return versionId().map(
                verId -> NOT_FORMATTED_VERSION_PAGE_URL.replace("{project_id}", projectId)
                        .replace("{version_id}", verId)
        );
    }

    public boolean isUpdateAvailable() {
        return version().flatMap(Version::kernel).map(ver -> ver.isGreater(mod.version())).orElse(false);
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
        return of(modrinthUpdateChecker, FailReason.NONE, versionMetadata);
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
