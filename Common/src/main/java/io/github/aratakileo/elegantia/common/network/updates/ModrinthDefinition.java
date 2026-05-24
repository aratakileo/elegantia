package io.github.aratakileo.elegantia.common.network.updates;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

public final class ModrinthDefinition {
    private final static String PAGE_LINK = "https://modrinth.com/mod/{}/version/{}";

    public final JsonObject data;

    private final String pageLink, version, versionId, downloadLink;
    private final Set<String> gameVersions;
    private final Set<String> loaders;

    public ModrinthDefinition(
            @NotNull JsonObject data,
            @NotNull String pageLink,
            @NotNull String version,
            @NotNull String versionId,
            @NotNull String downloadLink,
            @NotNull Set<String> gameVersions,
            @NotNull Set<String> loaders
    ) {
        this.data = data;
        this.pageLink = pageLink;
        this.version = version;
        this.versionId = versionId;
        this.downloadLink = downloadLink;
        this.gameVersions = gameVersions;
        this.loaders = loaders;
    }

    public String pageLink() {
        return pageLink;
    }

    public String version() {
        return version;
    }

    public String versionId() {
        return versionId;
    }

    public String downloadLink() {
        return downloadLink;
    }

    public Set<String> gameVersions() {
        return gameVersions;
    }

    public Set<String> loaders() {
        return loaders;
    }

    public static @NotNull ModrinthDefinition from(@NotNull JsonObject data, @NotNull String modId) {
        final var version = data.get("version_number").getAsString();
        final var versionId = data.get("id").getAsString();
        final var downloadLink = data.get("files").getAsJsonArray().get(0).getAsJsonObject().get("url").getAsString();

        final var gameVersions = data.get("game_versions")
                .getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toSet());

        final var loaders = data.get("loaders")
                .getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toSet());

        final var pageLink = Strings.format(PAGE_LINK, modId, versionId);

        return new ModrinthDefinition(data, pageLink, version, versionId, downloadLink, gameVersions, loaders);
    }
}
