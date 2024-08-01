package io.github.aratakileo.elegantia.util.updatechecker;

import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.exception.NoSuchModException;
import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.util.type.Namespace;
import io.github.aratakileo.elegantia.util.type.Platform;
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
import java.util.Objects;
import java.util.Optional;

public class ModrinthUpdateChecker {
    private final static Logger LOGGER = LoggerFactory.getLogger(ModrinthUpdateChecker.class);

    private final static String NOT_FORMATTED_REQUEST_URL =
            "https://api.modrinth.com/v2/project/{project_id}/version?game_versions=%5B%22{minecraft_version}%22%5D" +
                    "&loaders=%5B%22{platform}%22%5D";

    private final static String NOT_FORMATTED_VERSION_PAGE_URL
            = "https://modrinth.com/mod/{project_id}/version/{version_id}";

    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private final String modId, projectId, minecraftVersion;

    private @Nullable Response lastResponse = null;

    public ModrinthUpdateChecker(@NotNull Namespace modAndProjectNamespace) {
        this(modAndProjectNamespace.get(), modAndProjectNamespace.get());
    }

    public ModrinthUpdateChecker(@NotNull Namespace namespace, @NotNull String projectId) {
        this(namespace.get(), projectId, Platform.getMinecraftVersion());
    }

    public ModrinthUpdateChecker(
            @NotNull Namespace namespace,
            @NotNull String projectId,
            @NotNull String minecraftVersion
    ) {
        this.modId = namespace.get();
        this.projectId = projectId;
        this.minecraftVersion = minecraftVersion;
    }

    public ModrinthUpdateChecker(@NotNull String modAndProjectId) {
        this(modAndProjectId, modAndProjectId);
    }

    public ModrinthUpdateChecker(@NotNull String modId, @NotNull String projectId) {
        this(modId, projectId, Platform.getMinecraftVersion());
    }

    public ModrinthUpdateChecker(
            @NotNull String modId,
            @NotNull String projectId,
            @NotNull String minecraftVersion
    ) {
        this.modId = modId;
        this.projectId = projectId;
        this.minecraftVersion = minecraftVersion;
    }

    public @NotNull Response check() {
        return check(null);
    }

    public @NotNull Response check(@Nullable Platform platform) {
        final var mod = ModInfo.getOrThrow(modId);

        try {
            platform = Objects.requireNonNullElse(platform, mod.getKernelPlatform());

            final var requestHeader = getRequestHeader();
            final var request = HttpRequest.newBuilder(URI.create(getRequestUrl(platform)))
                    .setHeader("User-Agent", requestHeader)
                    .build();

            LOGGER.info(
                    "Checking updates for mod with id `{}` (modrinth project id: {}) with request header `{}` for {}",
                    modId,
                    projectId,
                    requestHeader,
                    "%s platform (minecraft v%s)".formatted(platform, minecraftVersion)
            );

            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());

            switch (basicResponse.statusCode()) {
                case 404 -> {
                    lastResponse = Response.of(ResponseCode.DOES_NOT_EXIST_AT_MODRINTH);
                    return lastResponse;
                }
                case 200 -> {}
                default -> throw new InvalidResponseCodeException(basicResponse.statusCode());
            }

            final var versionsMetadata = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionsMetadata.isEmpty()) {
                lastResponse = Response.of(ResponseCode.NO_VERSIONS_FOUND);
                return lastResponse;
            }

            final var versionInfo = versionsMetadata.get(0).getAsJsonObject();
            final var modrinthProjectVersion = versionInfo.get("version_number").getAsString();
            final var versionId = versionInfo.get("id").getAsString();
            final var modVersion = mod.getVersion();
            final var isNewVersionAvailable = Versions.isGreaterThan(
                    Versions.getVersionKernel(modVersion).orElseThrow(
                            () -> new InvalidVersionFormatException("`%s` of mod with id `%s`".formatted(
                                    modVersion,
                                    modId
                            ))
                    ),
                    Versions.getVersionKernel(modrinthProjectVersion).orElseThrow(
                            () -> new InvalidVersionFormatException("`%s` of modrinth project with id `%s`".formatted(
                                    modrinthProjectVersion,
                                    projectId
                            ))
                    )
            );

            lastResponse = Response.of(
                    isNewVersionAvailable ? ResponseCode.NEW_VERSION_IS_AVAILABLE : ResponseCode.SUCCESSFUL,
                    modrinthProjectVersion,
                    NOT_FORMATTED_VERSION_PAGE_URL.replace(
                            "{project_id}",
                            projectId
                    ).replace("{version_id}", versionId),
                    versionInfo.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString()
            );
        } catch (IOException | InvalidVersionFormatException | NoSuchModException | InterruptedException e) {
            LOGGER.error("Failed to check updates for mod with id `%s` (modrinth project id: %s) v%s".formatted(
                    modId,
                    projectId,
                    ModInfo.get(modId).map(ModInfo::getVersion).orElse("-unknown")
            ), e);

            lastResponse = Response.of(ResponseCode.FAILED);
        }

        return lastResponse;
    }

    public @NotNull Optional<Response> getLastResponse() {
        return Optional.ofNullable(lastResponse);
    }

    public @NotNull Optional<SuccessfulResponse> getLastResponseAsSuccessful() {
        return lastResponse instanceof SuccessfulResponse successfulResponse
                ? Optional.of(successfulResponse) : Optional.empty();
    }

    private @NotNull String getRequestUrl(@NotNull Platform platform) {
        return NOT_FORMATTED_REQUEST_URL.replace("{project_id}", projectId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    private @NotNull String getRequestHeader() {
        final var baseRequestHeader = getVersionedSourceUrl(ModInfo.get(Namespace.ELEGANTIA).orElseThrow()).orElseThrow();

        if (Namespace.ELEGANTIA.equals(modId))
            return baseRequestHeader;

        final var modInfo = ModInfo.get(modId).orElseThrow();

        return "%s for 3rd party mod %s".formatted(
                baseRequestHeader,
                getVersionedSourceUrl(modInfo).orElse("`%s` (mod id: %s)".formatted(modInfo.getName(), modId))
        );
    }

    public static @NotNull Response quickCheck(@NotNull Namespace modAndProjectNamespace) {
        return new ModrinthUpdateChecker(modAndProjectNamespace).check();
    }

    public static @NotNull Response quickCheck(@NotNull Namespace namespace, @NotNull String projectId) {
        return new ModrinthUpdateChecker(namespace, projectId).check();
    }

    public static @NotNull Response quickCheck(@NotNull String modAndProjectId) {
        return new ModrinthUpdateChecker(modAndProjectId).check();
    }

    public static @NotNull Response quickCheck(@NotNull String modId, @NotNull String projectId) {
        return new ModrinthUpdateChecker(modId, projectId).check();
    }

    private static @NotNull Optional<String> getVersionedSourceUrl(@NotNull ModInfo modInfo) {
        return modInfo.getSourcesUrl().map(sourceUrl -> "%s@%s".formatted(
                sourceUrl.strip().replaceFirst("^https?://", ""),
                modInfo.getVersion()
        ));
    }

    public static class InvalidVersionFormatException extends RuntimeException {
        public InvalidVersionFormatException(@NotNull String message) {
            super(message);
        }
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
