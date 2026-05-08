package io.github.aratakileo.elegantia.updatechecker;

import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.core.NoSuchModException;
import io.github.aratakileo.elegantia.core.ModInfo;
import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.elegantia.core.Platform;
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

    private @Nullable ModrinthResponse lastResponse = null;

    public final ModInfo mod;
    public final String projectId;

    public @NotNull Platform platform;
    public @NotNull String minecraftVersion;

    public ModrinthUpdateChecker(
            @NotNull Namespace namespace,
            @NotNull String projectId
    ) {
        this.mod = ModInfo.getOrThrow(namespace);
        this.projectId = projectId;
        this.minecraftVersion = Platform.getMinecraftVersion();
        this.platform = mod.kernelPlatform();
    }

    public ModrinthUpdateChecker(
            @NotNull String modId,
            @NotNull String projectId
    ) {
        this.mod = ModInfo.getOrThrow(modId);
        this.projectId = projectId;
        this.minecraftVersion = Platform.getMinecraftVersion();
        this.platform = mod.kernelPlatform();
    }

    public @NotNull ModrinthUpdateChecker setModKernelPlatform() {
        platform = mod.kernelPlatform();
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
                    "Checking updates for mod with id `{}` (modrinth.com/project/{}) with request header `{}` for {}",
                    mod.id(),
                    projectId,
                    requestHeader,
                    "%s platform (minecraft v%s)".formatted(platform, minecraftVersion)
            );

            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            if (basicResponse.statusCode() != 200)
                return reportUnacceptableResponse(basicResponse);

            final var versionMetadatas = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionMetadatas.isEmpty())
                return reportResponseFail(FailReason.NO_VERSIONS_FOUND);

            lastResponse = ModrinthResponse.ofSuccessful(
                    this,
                    versionMetadatas.get(0).getAsJsonObject()
            );

            return lastResponse;
        } catch (IOException | NoSuchModException | InterruptedException e) {
            LOGGER.error("Failed to check updates for mod with id `%s` (modrinth project id: %s) v%s".formatted(
                    mod.id(),
                    projectId,
                    mod.version()
            ), e);

           return reportResponseFail(FailReason.UNKNOWN);
        }
    }

    public @NotNull Optional<ModrinthResponse> getLastResponse() {
        return Optional.ofNullable(lastResponse);
    }

    private @NotNull ModrinthResponse reportResponseFail(@NotNull FailReason failReason) {
        lastResponse = ModrinthResponse.ofFailed(this, failReason);
        return lastResponse;
    }

    private @NotNull ModrinthResponse reportUnacceptableResponse(@NotNull HttpResponse<String> basicResponse) {
        LOGGER.warn(
                "Got unacceptable server response {} with body:\n{}",
                basicResponse.statusCode(),
                basicResponse.body()
        );

        return reportResponseFail(switch (basicResponse.statusCode()) {
            case 400 -> FailReason.BAD_REQUEST;
            case 403 -> FailReason.ACCESS_FORBIDDEN;
            case 404 -> FailReason.DOES_NOT_EXIST_AT_MODRINTH;
            case 502 -> FailReason.INTERNAL_MODRINTH_ERROR;
            default -> FailReason.UNKNOWN;
        });
    }

    private @NotNull String getRequestUrl(@NotNull Platform platform) {
        return NOT_FORMATTED_REQUEST_URL.replace("{project_id}", projectId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    private @NotNull String getRequestHeader() {
        final var baseRequestHeader = getVersionedSourcePath(ModInfo.get(Namespace.ELEGANTIA).orElseThrow()).orElseThrow();

        if (Namespace.ELEGANTIA.equals(mod.id()))
            return baseRequestHeader;

        final var modInfo = ModInfo.get(mod.id()).orElseThrow();

        return "%s for 3rd party mod %s".formatted(
                baseRequestHeader,
                getVersionedSourcePath(modInfo).orElse("`%s` (mod id: %s)".formatted(modInfo.name(), mod.id()))
        );
    }

    public static @NotNull ModrinthUpdateChecker of(@NotNull String modAndProjectId) {
        return new ModrinthUpdateChecker(modAndProjectId, modAndProjectId);
    }

    public static @NotNull ModrinthUpdateChecker of(@NotNull Namespace modAndProjectNamespace) {
        return new ModrinthUpdateChecker(modAndProjectNamespace, modAndProjectNamespace.get());
    }

    private static @NotNull Optional<String> getVersionedSourcePath(@NotNull ModInfo modInfo) {
        return modInfo.sourcesUrl().map(sourceUrl -> "%s@%s".formatted(
                sourceUrl.strip().replaceFirst("^https?://", ""),
                modInfo.version()
        ));
    }
}
