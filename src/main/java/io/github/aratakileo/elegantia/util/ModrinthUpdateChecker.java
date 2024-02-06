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

    private @NotNull BiPredicate<@Nullable String, @NotNull String> versionsComparator = DEFAULT_VERSION_COMPARATOR;

    private final String modId, minecraftVersion;
    private final Platform platform;

    public ModrinthUpdateChecker(@NotNull String modId) {
        this(modId, Platform.getCurrentMinecraftVersion());
    }

    public ModrinthUpdateChecker(@NotNull String modId, @NotNull String minecraftVersion) {
        this(modId, minecraftVersion, Platform.getCurrentPlatform());
    }

    public ModrinthUpdateChecker(@NotNull String modId, @NotNull String minecraftVersion, @NotNull Platform platform) {
        this.modId = modId;
        this.minecraftVersion = minecraftVersion;
        this.platform = platform;
    }

    public @NotNull ResponseCode check() {
        final var request = HttpRequest.newBuilder(URI.create(getFormattedUrl(NOT_FORMATTED_REQUEST_URL)))
                .setHeader(
                        "User-Agent",
                        "github.com/aratakileo/elegantia"
                                + '@'
                                + ModInfo.getVersion(Elegantia.MODID).orElse("unknown")
                                + " (aratakileo@gmail.com)"
                                + (
                                        modId.equals(Elegantia.MODID) ? "" : (
                                        " for third party mod `" + ModInfo.getName(modId).orElse("unknown") + '`')
                                )
                )
                .build();

        try {
            final var basicResponse = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            final var versionsMetadata = JsonParser.parseString(basicResponse.body()).getAsJsonArray();

            if (versionsMetadata.isEmpty()) return ResponseCode.DOES_NOT_EXIST_AT_MODRINTH;

            return versionsComparator.test(
                    ModInfo.getVersion(modId).orElse(null),
                    versionsMetadata.get(0).getAsJsonObject().get("version_number").getAsString()
            ) ? ResponseCode.NEW_VERSION_IS_AVAILABLE : ResponseCode.SUCCESSFUL;
        } catch (IOException | InterruptedException e) {
            Elegantia.LOGGER.error(
                    "Failed to check updates for `"
                            + ModInfo.getName(modId).orElse("unknown")
                            + "` (modId:" + modId +") v"
                            + ModInfo.getVersion(modId).orElse("-unknown"),
                    e
            );
        }

        return ResponseCode.FAILED;
    }

    public void setVersionsComparator(@NotNull BiPredicate<@Nullable String, @NotNull String> versionsComparator) {
        this.versionsComparator = versionsComparator;
    }

    public @NotNull String getFormattedUrl(@NotNull String notFormattedUrl) {
        return notFormattedUrl.replace("{project_id}", modId)
                .replace("{minecraft_version}", minecraftVersion)
                .replace("{platform}", platform.name().toLowerCase());
    }

    public enum ResponseCode {
        FAILED,
        DOES_NOT_EXIST_AT_MODRINTH,
        SUCCESSFUL,
        NEW_VERSION_IS_AVAILABLE
    }
}
