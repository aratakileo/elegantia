package io.github.aratakileo.elegantia.common.network.updates;

import io.github.aratakileo.elegantia.common.environment.ContactKey;
import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.common.network.ApiRequest;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;

public final class ModrinthUpdates {
    private final static ApiRequest.Builder REQUEST_BUILDER;

    public final ApiRequest request;

    private final String modId;
    private final boolean restrictRequest;

    private ModrinthUpdates(@NotNull ApiRequest request, @NotNull String modId, boolean restrictRequest) {
        this.request = request;
        this.modId = modId;
        this.restrictRequest = restrictRequest;
    }

    public @NotNull Result<ModrinthResponse> check() {
        if (restrictRequest) {
            logError();
            return Result.fromError(IllegalStateException::new, "invalid request arguments");
        }

        return request.wrappedInteract(ResponseStatus.UNKNOWN)
                .flatMap(source -> ModrinthResponse.from(source, modId));
    }

    public @NotNull CompletableFuture<Result<ModrinthResponse>> asyncCheck() {
        if (restrictRequest) {
            logError();

            return CompletableFuture.completedFuture(Result.fromError(
                    IllegalStateException::new,
                    "invalid request arguments"
            ));
        }

        return request.wrappedAsyncInteract(ResponseStatus.UNKNOWN)
                .thenApply(resolved -> resolved.flatMap(
                        source -> ModrinthResponse.from(source, modId)
                ));
    }

    public @NotNull String requesterInfo() {
        return request.source.headers().map().get("User-Agent").getFirst();
    }

    @Override
    public String toString() {
        return Strings.format("{}({})", ModrinthUpdates.class.getSimpleName(), request.link());
    }

    private void logError() {
        LoggerFactory.getLogger(modId)
                .error("the Modrinth update request was not fulfilled due to incorrect parameters");
    }

    public static @NotNull Builder builder(@NotNull String modId) {
        return new Builder(modId);
    }

    public static @NotNull Builder builder(@NotNull Origin modOrigin) {
        return new Builder(modOrigin.key);
    }

    public static final class Builder {
        private final String modId;

        private final HashSet<String> gameVersions = new HashSet<>();
        private final HashSet<String> loaders = new HashSet<>();

        private String requesterInfo = null;
        private Duration timeout = Duration.ofSeconds(10);
        private boolean restrictRequest = false;

        private Builder(@NotNull String modId) {
            this.modId = modId;
        }

        public @NotNull Builder addGameVersion(@NotNull String version) {
            gameVersions.add(version);
            return this;
        }

        public @NotNull Builder setCurrentGameVersion() {
            return setExactGameVersion(Loader.gameVersion());
        }

        public @NotNull Builder setExactGameVersion(@NotNull String version) {
            gameVersions.clear();
            gameVersions.add(version);
            return this;
        }

        public @NotNull Builder addLoader(@NotNull String loaderId) {
            loaders.add(loaderId);
            return this;
        }

        public @NotNull Builder addLoader(@NotNull Origin loaderOrigin) {
            loaders.add(loaderOrigin.key);
            return this;
        }

        public @NotNull Builder addLoader(@NotNull Loader loader) {
            loaders.add(loader.origin().key);
            return this;
        }

        public @NotNull Builder setCurrentLoader() {
            loaders.clear();
            loaders.add(Loader.current().origin().key);
            return this;
        }

        public @NotNull Builder setExactLoader(@NotNull String loaderId) {
            loaders.clear();
            loaders.add(loaderId);
            return this;
        }

        public @NotNull Builder setExactLoader(@NotNull Origin loaderOrigin) {
            loaders.clear();
            loaders.add(loaderOrigin.key);
            return this;
        }

        public @NotNull Builder setExactLoader(@NotNull Loader loader) {
            loaders.clear();
            loaders.add(loader.origin().key);
            return this;
        }

        public @NotNull Builder timeout(@NotNull Duration duration) {
            this.timeout = duration;
            return this;
        }

        public @NotNull Builder setRequesterInfo(@NotNull String modId) {
            final var requesterInfoBuilder = new StringBuilder("API wrapper https://github.com/aratakileo/elegantia")
                    .append(" [0.2.0] (aratakileo@gmail.com)");

            if (!Origin.ELEGANTIA.is(modId))
                requesterInfoBuilder.append(" used by ").append(createRequesterInfo(modId));

            requesterInfo = requesterInfoBuilder.toString();

            return this;
        }

        public @NotNull Builder setRequesterInfo(@NotNull Origin modOrigin) {
            return setRequesterInfo(modOrigin.key);
        }

        public @NotNull Builder setExactRequesterInfo(@NotNull String info) {
            requesterInfo = info;
            return this;
        }

        public @NotNull ModrinthUpdates build() {
            if (Exceptions.throwOrLogIf(
                    gameVersions.isEmpty() || loaders.isEmpty(),
                    modId,
                    IllegalStateException::new,
                    "no {} specified",
                    gameVersions.isEmpty() ? "game versions" : "loaders"
            )) restrictRequest = true;

            if (Exceptions.throwOrLogIf(
                    requesterInfo == null || requesterInfo.isBlank(),
                    modId,
                    IllegalStateException::new,
                    "no requester info specified"
            )) restrictRequest = true;

            final var requestBuilder = REQUEST_BUILDER.copy().addPathSegment(modId);

            if (!restrictRequest) {
                requestBuilder.addPathSegment("version")
                        .putQueryJsonifiedParam("game_versions", gameVersions)
                        .putQueryJsonifiedParam("loaders", loaders)
                        .putUserAgent(requesterInfo)
                        .timeout(timeout);
            } else requestBuilder.addPathSegment("invalidRequest")
                    .putUserAgent("Invalid-Requester-Fallback");

            return new ModrinthUpdates(requestBuilder.build(), modId, restrictRequest);
        }

        private @NotNull String createRequesterInfo(@NotNull String modId) {
            final var result = Loader.modResult(modId).throwOrLogIfError(modId);

            if (result.isError()) {
                restrictRequest = true;
                return "";
            }

            final var modProfile = result.unwrap();
            final var contacts = modProfile.contacts().get(ContactKey.EMAIL, ContactKey.ISSUES);

            if (Exceptions.throwOrLogIf(
                    contacts.isEmpty(),
                    modId,
                    PoorContactInformationException::new,
                    "the mod `{}` contact information does not contain both `email` and `issues` keys",
                    modProfile.name()
            )) {
                restrictRequest = true;
                return "";
            }

            final var builder = new StringBuilder("3rd party mod `");

            builder.append(modProfile.name())
                    .append("` v")
                    .append(modProfile.versionName())
                    .append(", contact info: ")
                    .append(contacts.stream().findFirst().get());

            if (contacts.size() > 1)
                builder.append(" or ").append(contacts.stream().toList().getLast());

            return builder.toString();
        }
    }

    static {
        REQUEST_BUILDER = ApiRequest.builder("https://api.modrinth.com/v2/project");
    }

    public static class PoorContactInformationException extends Exception {
        public PoorContactInformationException(@NotNull String message) {
            super(message);
        }
    }
}
