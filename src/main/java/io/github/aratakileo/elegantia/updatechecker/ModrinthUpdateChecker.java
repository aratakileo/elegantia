package io.github.aratakileo.elegantia.updatechecker;

import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.core.NoSuchModException;
import io.github.aratakileo.elegantia.core.ModInfo;
import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.elegantia.core.Platform;
import io.github.aratakileo.elegantia.util.Versions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class ModrinthUpdateChecker {
    private final static Logger LOGGER = LoggerFactory.getLogger(ModrinthUpdateChecker.class);

    private final static String NOT_FORMATTED_REQUEST_URL =
            "https://api.modrinth.com/v2/project/{project_id}/version?game_versions=%5B%22{minecraft_version}%22%5D" +
                    "&loaders=%5B%22{platform}%22%5D";

    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    protected final ModInfo mod;
    protected final String projectId;

    private @Nullable ModrinthResponse lastResponse = null;

    public @NotNull Platform platform;
    public @NotNull String minecraftVersion;

    public ModrinthUpdateChecker(
            @NotNull Namespace namespace,
            @NotNull String projectId
    ) {
        this.mod = ModInfo.getOrThrow(namespace);
        this.projectId = projectId;
        this.minecraftVersion = Platform.getMinecraftVersion();
        this.platform = mod.getKernelPlatform();
    }

    public ModrinthUpdateChecker(
            @NotNull String modId,
            @NotNull String projectId
    ) {
        this.mod = ModInfo.getOrThrow(modId);
        this.projectId = projectId;
        this.minecraftVersion = Platform.getMinecraftVersion();
        this.platform = mod.getKernelPlatform();
    }

    public @NotNull ModrinthUpdateChecker setModKernelPlatform() {
        platform = mod.getKernelPlatform();
        return this;
    }

    public @NotNull ModrinthUpdateChecker setPlatform(@NotNull Platform platform) {
        this.platform = platform;
        return this;
    }

    public @NotNull ModrinthUpdateChecker setMinecraftCurrentVersion() {
        minecraftVersion = Platform.getMinecraftVersion();
        return this;
    }

    public @NotNull ModrinthUpdateChecker setMinecraftVersion(@NotNull String minecraftVersion) {
        this.minecraftVersion = minecraftVersion;
        return this;
    }

    public @NotNull ModrinthResponse check() {
        try {
            final var requestHeader = getRequestHeader();
            final var request = HttpRequest.newBuilder(URI.create(getRequestUrl(platform)))
                    .setHeader("User-Agent", requestHeader)
                    .build();

            LOGGER.info(
                    "Checking updates for mod with id `{}` (modrinth project id: {}) with request header `{}` for {}",
                    mod.getId(),
                    projectId,
                    requestHeader,
                    "%s platform (minecraft v%s)".formatted(platform, minecraftVersion)
            );

            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            switch (basicResponse.statusCode()) {
                case 404 -> {
                    lastResponse = ModrinthResponse.ofFailed(
                            this,
                            FailReason.DOES_NOT_EXIST_AT_MODRINTH
                    );
                    return lastResponse;
                }
                case 200 -> {}
                default -> throw new InvalidResponseCodeException(basicResponse.statusCode());
            }

            final var versionsMetadata = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionsMetadata.isEmpty()) {
                lastResponse = ModrinthResponse.ofFailed(this, FailReason.NO_VERSIONS_FOUND);
                return lastResponse;
            }

            lastResponse = ModrinthResponse.ofSuccessful(
                    this,
                    versionsMetadata.get(0).getAsJsonObject()
            );
        } catch (IOException | Versions.InvalidVersionFormatException | NoSuchModException | InterruptedException e) {
            LOGGER.error("Failed to check updates for mod with id `%s` (modrinth project id: %s) v%s".formatted(
                    mod.getId(),
                    projectId,
                    mod.getVersion()
            ), e);

            lastResponse = ModrinthResponse.ofFailed(this, FailReason.UNKNOWN);
        }

        return lastResponse;
    }

    public @NotNull Optional<ModrinthResponse> getLastResponse() {
        return Optional.ofNullable(lastResponse);
    }

    private @NotNull String getRequestUrl(@NotNull Platform platform) {
        return NOT_FORMATTED_REQUEST_URL.replace("{project_id}", projectId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    private @NotNull String getRequestHeader() {
        final var baseRequestHeader = getVersionedSourcePath(ModInfo.get(Namespace.ELEGANTIA).orElseThrow()).orElseThrow();

        if (Namespace.ELEGANTIA.equals(mod.getId()))
            return baseRequestHeader;

        final var modInfo = ModInfo.get(mod.getId()).orElseThrow();

        return "%s for 3rd party mod %s".formatted(
                baseRequestHeader,
                getVersionedSourcePath(modInfo).orElse("`%s` (mod id: %s)".formatted(modInfo.getName(), mod.getId()))
        );
    }

    public static @NotNull ModrinthUpdateChecker of(@NotNull String modAndProjectId) {
        return new ModrinthUpdateChecker(modAndProjectId, modAndProjectId);
    }

    public static @NotNull ModrinthUpdateChecker of(@NotNull Namespace modAndProjectNamespace) {
        return new ModrinthUpdateChecker(modAndProjectNamespace, modAndProjectNamespace.get());
    }

    private static @NotNull Optional<String> getVersionedSourcePath(@NotNull ModInfo modInfo) {
        return modInfo.getSourcesUrl().map(sourceUrl -> "%s@%s".formatted(
                sourceUrl.strip().replaceFirst("^https?://", ""),
                modInfo.getVersion()
        ));
    }

    public static class InvalidResponseCodeException extends RuntimeException {
        public InvalidResponseCodeException(int code) {
            super(String.valueOf(code));
        }

        public InvalidResponseCodeException(@NotNull String message) {
            super(message);
        }
    }
}
