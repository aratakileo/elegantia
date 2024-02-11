package io.github.aratakileo.elegantia.updatechecker;

import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.util.Platform;
import io.github.aratakileo.elegantia.util.Versions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiPredicate;

public class ModrinthUpdateChecker {
    private final static String NOT_FORMATTED_REQUEST_URL =
            "https://api.modrinth.com/v2/project/{project_id}/version?game_versions=%5B%22{minecraft_version}%22%5D" +
                    "&loaders=%5B%22{platform}%22%5D";

    private final static String NOT_FORMATTED_VERSION_PAGE_URL
            = "https://modrinth.com/mod/{project_id}/version/{version_id}";

    public final static BiPredicate<@Nullable String, @NotNull String> DEFAULT_VERSION_COMPARATOR = (
            currentVersion,
            latestVersion
    ) -> Objects.nonNull(currentVersion) && Versions.isGreaterThan(
            Versions.getVersionNumber(currentVersion),
            Versions.getVersionNumber(latestVersion)
    );

    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final static String REQUEST_HEADER = "github.com/aratakileo/elegantia@%s";

    private final String modId, projectId, minecraftVersion;
    private final Platform platform;

    private @NotNull BiPredicate<@Nullable String, @NotNull String> versionsComparator = DEFAULT_VERSION_COMPARATOR;
    private @Nullable Response lastResponse = null;

    public ModrinthUpdateChecker(@NotNull String modAndProjectId) {
        this(modAndProjectId, modAndProjectId);
    }

    public ModrinthUpdateChecker(@NotNull String modAndProjectId, @NotNull Platform platform) {
        this(modAndProjectId, modAndProjectId, platform);
    }

    public ModrinthUpdateChecker(@NotNull String modId, @NotNull String projectId) {
        this(modId, projectId, Platform.getCurrentPlatform());
    }

    public ModrinthUpdateChecker(@NotNull String modId, @NotNull String projectId, @NotNull Platform platform) {
        this(modId, projectId, Platform.getCurrentMinecraftVersion(), platform);
    }

    public ModrinthUpdateChecker(
            @NotNull String modId,
            @NotNull String projectId,
            @NotNull String minecraftVersion,
            @NotNull Platform platform
    ) {
        this.modId = modId;
        this.projectId = projectId;
        this.minecraftVersion = minecraftVersion;
        this.platform = platform;
    }

    public @NotNull Response check() {
        final var request = HttpRequest.newBuilder(URI.create(getRequestUrl()))
                .setHeader("User-Agent", getRequestHeader())
                .build();

        try {
            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            final var versionsMetadata = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionsMetadata.isEmpty()) {
                lastResponse = Response.of(ResponseCode.DOES_NOT_EXIST_AT_MODRINTH);
                return lastResponse;
            }

            final var versionInfo = versionsMetadata.get(0).getAsJsonObject();
            final var versionNumber = versionInfo.get("version_number").getAsString();
            final var versionId = versionInfo.get("id").getAsString();

            lastResponse = Response.of(
                    versionsComparator.test(
                            ModInfo.getVersion(modId).orElse(null),
                            versionNumber
                    ) ? ResponseCode.NEW_VERSION_IS_AVAILABLE : ResponseCode.SUCCESSFUL,
                    versionNumber,
                    NOT_FORMATTED_VERSION_PAGE_URL.replace(
                            "{project_id}",
                            projectId
                    ).replace("{version_id}", versionId),
                    versionInfo.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString()
            );
        } catch (IOException | InterruptedException e) {
            Elegantia.LOGGER.error("Failed to check updates for `%s` (mod id: %s, project id: %s) v%s".formatted(
                    ModInfo.getName(modId).orElse("unknown"),
                    modId,
                    projectId,
                    ModInfo.getVersion(modId).orElse("-unknown")
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

    public void setVersionsComparator(@NotNull BiPredicate<@Nullable String, @NotNull String> versionsComparator) {
        this.versionsComparator = versionsComparator;
    }

    private @NotNull String getRequestUrl() {
        return NOT_FORMATTED_REQUEST_URL.replace("{project_id}", projectId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    private @NotNull String getRequestHeader() {
        final var baseRequestHeader = REQUEST_HEADER.formatted(
                ModInfo.getVersion(Elegantia.MODID).orElse("unknown")
        );

        if (modId.equals(Elegantia.MODID))
            return baseRequestHeader;

        final var requestHeaderBuilder = new StringBuilder("%s for third party mod `%s`".formatted(
                baseRequestHeader,
                ModInfo.getName(modId).orElse("unknown")
        ));

        ModInfo.getSourcesUrl(modId).ifPresent(sourceUrl -> requestHeaderBuilder.append("(%s)".formatted(
                sourceUrl.startsWith("https://") ? sourceUrl.substring(8) : sourceUrl
        )));

        return requestHeaderBuilder.toString();
    }
}
