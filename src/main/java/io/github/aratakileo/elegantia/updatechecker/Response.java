package io.github.aratakileo.elegantia.updatechecker;

import org.jetbrains.annotations.NotNull;

public class Response {
    private final ResponseCode responseCode;

    protected Response(@NotNull ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public @NotNull ResponseCode getResponseCode() {
        return responseCode;
    }

    public boolean isFailed() {
        return responseCode.isFailed();
    }

    public boolean doesNotExistAtModrinth() {
        return responseCode.doesNotExistAtModrinth();
    }

    public boolean isSuccessful() {
        return responseCode.isSuccessful();
    }

    public boolean isNewVersionAvailable() {
        return responseCode.isNewVersionAvailable();
    }

    public static @NotNull Response of(@NotNull ResponseCode responseCode) {
        return new Response(responseCode);
    }

    public static @NotNull Response of(
            @NotNull ResponseCode responseCode,
            @NotNull String definedVersion,
            @NotNull String versionPageUrl,
            @NotNull String downloadUrl
    ) {
        return new SuccessfulResponse(responseCode, definedVersion, versionPageUrl, downloadUrl);
    }
}
