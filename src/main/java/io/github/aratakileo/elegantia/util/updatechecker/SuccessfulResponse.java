package io.github.aratakileo.elegantia.util.updatechecker;

import io.github.aratakileo.elegantia.util.Strings;
import org.jetbrains.annotations.NotNull;

public class SuccessfulResponse extends Response {
    private final @NotNull String definedVersion, versionPageUrl, downloadUrl;

    protected SuccessfulResponse(
            @NotNull ResponseCode responseCode,
            @NotNull String definedVersion,
            @NotNull String versionPageUrl,
            @NotNull String downloadUrl
    ) {
        super(responseCode);

        this.definedVersion = definedVersion;
        this.versionPageUrl = versionPageUrl;
        this.downloadUrl = downloadUrl;
    }

    public @NotNull String getDefinedVersion() {
        return definedVersion;
    }

    public @NotNull String getVersionPageUrl() {
        return versionPageUrl;
    }

    public @NotNull String getDownloadUrl() {
        return downloadUrl;
    }

    @Override
    public String toString() {
        return "SuccessfulResponse{responseCode=%s, definedVersion=%s, versionPageUrl=%s, downloadUrl=%s}".formatted(
                responseCode,
                Strings.repr(definedVersion),
                Strings.repr(versionPageUrl),
                Strings.repr(downloadUrl)
        );
    }
}
