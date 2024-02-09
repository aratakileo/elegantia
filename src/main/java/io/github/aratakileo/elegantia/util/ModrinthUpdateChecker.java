package io.github.aratakileo.elegantia.util;

import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.Elegantia;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.module.ModuleDescriptor.Version;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.function.BiPredicate;

public class ModrinthUpdateChecker {
    public final static String NOT_FORMATTED_REQUEST_URL =
            "https://api.modrinth.com/v2/project/{project_id}/version?game_versions=%5B%22{minecraft_version}%22%5D" +
                    "&loaders=%5B%22{platform}%22%5D";

    public final static String NOT_FORMATTED_UPDATE_PAGE_URL
            = "https://modrinth.com/mod/{project_id}/versions?g={minecraft_version}&l={platform}";

    public final static BiPredicate<@Nullable String, @NotNull String> DEFAULT_VERSION_COMPARATOR = (
            currentVersion,
            latestVersion
    ) -> Objects.nonNull(currentVersion) && Version.parse(currentVersion).compareTo(Version.parse(latestVersion)) >= 1;

    private final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();
    private final static String REQUEST_HEADER = "github.com/aratakileo/elegantia@{version}";

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
        final var request = HttpRequest.newBuilder(URI.create(getFormattedUrl(NOT_FORMATTED_REQUEST_URL)))
                .setHeader("User-Agent", getRequestHeader())
                .build();

        try {
            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            final var versionsMetadata = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionsMetadata.isEmpty()) {
                lastResponse = new Response(ResponseCode.DOES_NOT_EXIST_AT_MODRINTH);
                return lastResponse;
            }

            final var definedVersion = versionsMetadata.get(0).getAsJsonObject().get("version_number").getAsString();

            lastResponse = new Response(
                    versionsComparator.test(
                            ModInfo.getVersion(modId).orElse(null),
                            definedVersion
                    ) ? ResponseCode.NEW_VERSION_IS_AVAILABLE : ResponseCode.SUCCESSFUL,
                    definedVersion
            );
        } catch (IOException | InterruptedException e) {
            Elegantia.LOGGER.error(
                    "Failed to check updates for `"
                            + ModInfo.getName(modId).orElse("unknown")
                            + "` (mod id:"
                            + modId
                            + ", project id: "
                            + projectId
                            + ") v"
                            + ModInfo.getVersion(modId).orElse("-unknown"),
                    e
            );

            lastResponse = new Response(ResponseCode.FAILED);
        }

        return lastResponse;
    }

    public @Nullable Response getLastResponse() {
        return lastResponse;
    }

    public void setVersionsComparator(@NotNull BiPredicate<@Nullable String, @NotNull String> versionsComparator) {
        this.versionsComparator = versionsComparator;
    }

    public @NotNull String getFormattedUrl(@NotNull String notFormattedUrl) {
        return notFormattedUrl.replace("{project_id}", projectId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    private @NotNull String getRequestHeader() {
        final var baseRequestHeader = REQUEST_HEADER.replace(
                "{version}",
                ModInfo.getVersion(Elegantia.MODID).orElse("unknown")
        );

        if (modId.equals(Elegantia.MODID))
            return baseRequestHeader;

        final var requestHeaderBuilder = new StringBuilder(baseRequestHeader)
                .append(" for third party mod `")
                .append(ModInfo.getName(modId).orElse("unknown"))
                .append('`');

        ModInfo.getSourcesUrl(modId).ifPresent(
                sourceUrl -> requestHeaderBuilder.append('(')
                        .append(sourceUrl.startsWith("https://") ? sourceUrl.substring(8) : sourceUrl)
                        .append(')')
        );

        return requestHeaderBuilder.toString();
    }

    public static class Response {
        private final ResponseCode responseCode;
        private final @Nullable String definedVersion;

        public Response(@NotNull ResponseCode responseCode) {
            this(responseCode, null);
        }

        public Response(@NotNull ResponseCode responseCode, @Nullable String definedVersion) {
            this.responseCode = responseCode;
            this.definedVersion = definedVersion;
        }

        public @Nullable String getDefinedVersion() {
            return definedVersion;
        }

        public @NotNull ResponseCode getResponseCode() {
            return responseCode;
        }
    }

    public enum ResponseCode {
        FAILED,
        DOES_NOT_EXIST_AT_MODRINTH,
        SUCCESSFUL,
        NEW_VERSION_IS_AVAILABLE;

        public boolean isFailed() {
            return this == FAILED;
        }

        public boolean doesNotExistAtModrinth() {
            return this == DOES_NOT_EXIST_AT_MODRINTH;
        }

        public boolean isSuccessful() {
            return this == SUCCESSFUL;
        }

        public boolean isNewVersionAvailable() {
            return this == NEW_VERSION_IS_AVAILABLE;
        }
    }
}
