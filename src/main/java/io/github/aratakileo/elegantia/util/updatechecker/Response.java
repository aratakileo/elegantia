package io.github.aratakileo.elegantia.util.updatechecker;

import org.jetbrains.annotations.NotNull;

public class Response {
    public final ResponseCode responseCode;

    protected Response(@NotNull ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public boolean isFailed() {
        return responseCode.isFailed();
    }

    public boolean doesNotExistAtModrinth() {
        return responseCode.doesNotExistAtModrinth();
    }

    public boolean isNoVersionsFound() {
        return responseCode.isNoVersionsFound();
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

    @Override
    public String toString() {
        return "Response{responseCode=%s}".formatted(responseCode);
    }
}
