package io.github.aratakileo.elegantia.client.text;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import io.github.aratakileo.elegantia.util.type.Either;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashMap;

public final class MatcherHandler {
    private static final HashMap<ResourceLocation, Matcher> MATCHERS = new HashMap<>();

    private static final LoadingCache<Either<String, Component>, MatchContext> CACHE = CacheBuilder.newBuilder()
            .maximumSize(250)
            .expireAfterAccess(Duration.ofMinutes(5))
            .build(CacheLoader.from(textValue -> {
                final var context = textValue.map(MatchContext::of, MatchContext::of);

                for (final var matcher: MATCHERS.values())
                    matcher.match(context);

                return context;
            }));

    private MatcherHandler() {}

    public static @NotNull MatchContext getMatched(@NotNull String text) {
        return CACHE.getUnchecked(Either.ofLeft(text));
    }

    public static @NotNull MatchContext getMatched(@NotNull Component text) {
        return CACHE.getUnchecked(Either.ofRight(text));
    }

    public static void putMatcher(@NotNull Matcher matcher) {
        MATCHERS.put(matcher.id, matcher);
    }
}
