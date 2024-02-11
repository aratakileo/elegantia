package io.github.aratakileo.elegantia.updatechecker;

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
