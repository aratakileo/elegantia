package io.github.aratakileo.elegantia.common.network.updates;

import io.github.aratakileo.elegantia.common.network.CodeAssociated;
import org.jetbrains.annotations.Nullable;

public enum ResponseStatus implements CodeAssociated {
    OK(200),
    BAD_REQUEST(400),
    ACCESS_FORBIDDEN(403),
    DOES_NOT_EXIST(404),
    INTERNAL_ERROR(502),
    NO_VERSIONS_FOUND,
    UNKNOWN;

    private final Integer code;

    ResponseStatus(int code) {
        this.code = code;
    }

    ResponseStatus() {
        this.code = null;
    }

    @Override
    public @Nullable Integer code() {
        return code;
    }
}
