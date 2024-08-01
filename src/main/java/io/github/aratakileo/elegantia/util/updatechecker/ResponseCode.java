package io.github.aratakileo.elegantia.util.updatechecker;

public enum ResponseCode {
    FAILED,
    DOES_NOT_EXIST_AT_MODRINTH,
    NO_VERSIONS_FOUND,
    SUCCESSFUL,
    NEW_VERSION_IS_AVAILABLE;

    public boolean isFailed() {
        return this == FAILED;
    }

    public boolean doesNotExistAtModrinth() {
        return this == DOES_NOT_EXIST_AT_MODRINTH;
    }

    public boolean isNoVersionsFound() {
        return this == NO_VERSIONS_FOUND;
    }

    public boolean isSuccessful() {
        return this == SUCCESSFUL;
    }

    public boolean isNewVersionAvailable() {
        return this == NEW_VERSION_IS_AVAILABLE;
    }
}
