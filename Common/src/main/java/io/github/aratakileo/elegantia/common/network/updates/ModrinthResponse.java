package io.github.aratakileo.elegantia.common.network.updates;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.common.network.AssociatedWrapper;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.List;

public final class ModrinthResponse {
    public final HttpResponse<String> response;

    private final ResponseStatus status;
    private final List<ModrinthDefinition> definitions;

    private ModrinthResponse(
            @NotNull HttpResponse<String> response,
            @NotNull ResponseStatus status,
            @NotNull List<ModrinthDefinition> definitions
    ) {
        this.response = response;
        this.status = status;
        this.definitions = definitions;
    }

    public @NotNull ResponseStatus status() {
        return status;
    }

    public @NotNull List<ModrinthDefinition> definitions() {
        return definitions;
    }

    public @NotNull ModrinthDefinition firstDefinition() {
        return definitions.getFirst();
    }

    public static @NotNull Result<ModrinthResponse> from(
            @NotNull AssociatedWrapper<ResponseStatus> source,
            @NotNull String modId
    ) {
        if (source.status() != ResponseStatus.OK)
            return Result.fromOk(new ModrinthResponse(source.response, source.status(), List.of()));

        return source.bodyArray().map(bodyArray -> {
            if (bodyArray.isEmpty())
                return new ModrinthResponse(source.response, ResponseStatus.NO_VERSIONS_FOUND, List.of());

            return new ModrinthResponse(
                    source.response,
                    source.status(),
                    bodyArray.asList()
                            .stream()
                            .map(item -> ModrinthDefinition.from(item.getAsJsonObject(), modId))
                            .toList()
            );
        });
    }
}
