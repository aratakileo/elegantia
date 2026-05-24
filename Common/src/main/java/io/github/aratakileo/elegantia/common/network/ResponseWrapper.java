package io.github.aratakileo.elegantia.common.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;

public class ResponseWrapper {
    public final HttpResponse<String> response;

    public ResponseWrapper(@NotNull HttpResponse<String> response) {
        this.response = response;
    }

    public int statusCode() {
        return response.statusCode();
    }

    public @NotNull Result<JsonObject> bodyObject(@NotNull String key) {
        return bodyObject().map(obj -> obj.getAsJsonObject(key));
    }

    public @NotNull Result<JsonObject> bodyObject() {
        return Result.fromFactory(() -> JsonParser.parseString(response.body()).getAsJsonObject());
    }

    public @NotNull Result<JsonArray> bodyArray() {
        return Result.fromFactory(() -> JsonParser.parseString(response.body()).getAsJsonArray());
    }
}
