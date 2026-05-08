package io.github.aratakileo.elegantia.updatechecker;

public enum FailReason {
    NONE,
    UNKNOWN,
    DOES_NOT_EXIST_AT_MODRINTH,
    NO_VERSIONS_FOUND,
    INTERNAL_MODRINTH_ERROR,
    ACCESS_FORBIDDEN,
    BAD_REQUEST
}
