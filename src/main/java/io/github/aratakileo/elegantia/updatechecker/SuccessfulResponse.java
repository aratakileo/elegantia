package io.github.aratakileo.elegantia.updatechecker;

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
}
