package io.github.aratakileo.elegantia.common.network;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.stream.Collectors;

public class ApiRequest {
    public final static HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public final HttpRequest source;

    public ApiRequest(@NotNull HttpRequest source) {
        this.source = source;
    }

    public @NotNull Result<HttpResponse<String>> interact() {
        return interact(HttpResponse.BodyHandlers.ofString());
    }

    public <T> @NotNull Result<HttpResponse<T>> interact(@NotNull HttpResponse.BodyHandler<T> bodyHandler) {
        return Result.fromFactory(() -> HTTP_CLIENT.send(source, bodyHandler));
    }

    public @NotNull Result<ResponseWrapper> wrappedInteract() {
        return interact().map(ResponseWrapper::new);
    }

    public <T extends Enum<T> & CodeAssociated> @NotNull Result<AssociatedWrapper<T>> wrappedInteract(
            @NotNull T defaultAssociation
    ) {
        return interact().map(response -> new AssociatedWrapper<>(response, defaultAssociation));
    }

    public @NotNull CompletableFuture<Result<HttpResponse<String>>> asyncInteract() {
        return asyncInteract(HttpResponse.BodyHandlers.ofString());
    }

    public <T> @NotNull CompletableFuture<Result<HttpResponse<T>>> asyncInteract(@NotNull HttpResponse.BodyHandler<T> bodyHandler) {
        return HTTP_CLIENT.sendAsync(source, bodyHandler)
                .thenApply(Result::fromOk)
                .exceptionally(throwable -> {
                    final var actualError = (throwable instanceof CompletionException)
                            ? throwable.getCause()
                            : throwable;

                    return Result.fromError(actualError);
                });
    }

    public @NotNull CompletableFuture<Result<ResponseWrapper>> wrappedAsyncInteract() {
        // Вызываем базовый асинхронный метод и маппим внутренность Result, если он успешен
        return asyncInteract().thenApply(result -> result.map(ResponseWrapper::new));
    }

    public <T extends Enum<T> & CodeAssociated>
    @NotNull CompletableFuture<Result<AssociatedWrapper<T>>> wrappedAsyncInteract(
            @NotNull T defaultAssociation
    ) {
        return asyncInteract().thenApply(result -> result.map(
                response -> new AssociatedWrapper<>(response, defaultAssociation)
        ));
    }

    public @NotNull String link() {
        return source.uri().toString();
    }

    public @NotNull String rawLink() {
        return URLDecoder.decode(link(), StandardCharsets.UTF_8);
    }

    /**
     * A use example:
     * <pre> {@code
      * System.out.println(
      *     ApiRequest.builder("https://example.com?sixtyNine")
      *         .putQueryParam("target", "you")
      *         .build()
      *         .link()
      * ); // approximate output: https://example.com?sixtyNine&target=you
      * }
     */
    public static final class Builder {
        public final String link;

        private final HashSet<String> queryEntries = new HashSet<>();
        private final HashMap<String, String> headerEntries = new HashMap<>();
        private final ArrayList<String> pathSegments = new ArrayList<>();

        private Duration timeout = Duration.ofSeconds(10);

        private Builder(@NotNull String link) {
            this.link = link;
        }

        /**
         *
         * Says builder to add query parameter at the end of {@link Builder#link}.
         * <p>
         * Example:
         * <pre>{@code
         * System.out.println(
         *     ApiRequest.builder("https://example.com")
         *         .putQueryParam("msg", "Привет!")
         *         .putQueryParam("version", 69)
         *         .putQueryParam("raw", true)
         *         .build()
         *         .link()
         * ); // approximate output: https://example.com?msg=%D0%9F%D1%80%D0%B8%D0%B2%D0%B5%D1%82%21&version=69&raw=true
         * }
         *
         * @param name the query parameter name
         * @param value the query parameter value
         * @return {@link Builder} itself
         */
        public @NotNull Builder putQueryParam(@NotNull String name, @NotNull Object value) {
            queryEntries.add(name + '=' + URLEncoder.encode(value.toString(), StandardCharsets.UTF_8));

            return this;
        }

        /**
         *
         * Says builder to add query parameter at the end of {@link Builder#link}.
         * <p>
         * Example:
         * <pre> {@code
         * final var request = ApiRequest.builder("https://example.com")
         *         .putQueryPipedParam("languages", "ru", "en", "de")
         *         .build();
         *
         * System.out.println(request.link()); // output: https://example.com?languages=ru%7Cen%7Cde
         * System.out.println(request.rawLink()); // output: https://example.com?languages=ru|en|de
         * }
         *
         * @param name the query parameter name
         * @param values the query parameter values
         * @return {@link Builder} itself
         */
        public @NotNull Builder putQueryPipedParam(@NotNull String name, @NotNull Object @NotNull... values) {
            final var pipedValues = Arrays.stream(values)
                    .map(Object::toString)
                    .collect(Collectors.joining("|"));

            queryEntries.add(name + '=' + URLEncoder.encode(pipedValues, StandardCharsets.UTF_8));

            return this;
        }

        /**
         *
         * Says builder to add query parameter at the end of {@link Builder#link}.
         * <p>
         * Example:
         * <pre> {@code
         * final var request = ApiRequest.builder("https://example.com")
         *         .putQueryJsonifiedParam("phrase", "Who let the dogs out?")
         *         .build();
         *
         * System.out.println(request.link()); // output: https://example.com?phrase=%22Who+let+the+dogs+out%3F%22
         * System.out.println(request.rawLink()); // output: https://example.com?phrase="Who let the dogs out?"
         * }
         *
         * @param name the query parameter name
         * @param value the query parameter value
         * @return {@link Builder} itself
         */
        public @NotNull Builder putQueryJsonifiedParam(@NotNull String name, @Nullable Object value) {
            queryEntries.add(name + '=' + URLEncoder.encode(Strings.jsonify(value), StandardCharsets.UTF_8));

            return this;
        }

        public @NotNull Builder putHeader(@NotNull String name, @NotNull String value) {
            headerEntries.put(name, value);

            return this;
        }

        public @NotNull Builder putUserAgent(@NotNull String value) {
            return putHeader("User-Agent", value);
        }

        public @NotNull Builder addPathSegment(@NotNull String value) {
            pathSegments.add(value);
            return this;
        }

        public @NotNull Builder timeout(@NotNull Duration duration) {
            timeout = duration;
            return this;
        }

        public @NotNull Builder copy() {
            final var copy = new Builder(link);

            copy.queryEntries.addAll(queryEntries);
            copy.headerEntries.putAll(headerEntries);
            copy.pathSegments.addAll(pathSegments);
            copy.timeout = timeout;

            return copy;
        }

        public @NotNull ApiRequest build() {
            final var linkBuilder = new StringBuilder();
            final var pathFinishIndex = link.indexOf('?');

            if (pathFinishIndex == -1 || pathSegments.isEmpty()) linkBuilder.append(link);
            else {
                linkBuilder.append(link, 0, pathFinishIndex);

                var lastSegmentEndsWithSlash = linkBuilder.toString().endsWith("/");

                for (final var segment: pathSegments) {
                    if (!lastSegmentEndsWithSlash)
                        linkBuilder.append('/');
                    else lastSegmentEndsWithSlash = false;

                    linkBuilder.append(
                            Arrays.stream(segment.split("/+"))
                                    .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                                    .collect(Collectors.joining("/"))
                    );
                }

                linkBuilder.append(link.substring(pathFinishIndex));
            }

            if (!queryEntries.isEmpty()) {
                if (pathFinishIndex == -1) linkBuilder.append('?');
                else linkBuilder.append('&');

                linkBuilder.append(String.join("&", queryEntries));
            }

            final var requestBuilder = HttpRequest.newBuilder(URI.create(linkBuilder.toString()))
                    .timeout(timeout);

            for (final var headerEntry: headerEntries.entrySet())
                requestBuilder.setHeader(headerEntry.getKey(), headerEntry.getValue());

            return new ApiRequest(requestBuilder.build());
        }
    }

    public static @NotNull Builder builder(@NotNull String link) {
        return new Builder(link);
    }
}
