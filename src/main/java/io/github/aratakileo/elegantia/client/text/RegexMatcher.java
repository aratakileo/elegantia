package io.github.aratakileo.elegantia.client.text;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;

public class RegexMatcher extends Matcher {
    public final Pattern pattern;
    public final Function<MatchResult, Match> match;

    public RegexMatcher(
            @NotNull ResourceLocation id,
            @NotNull String pattern,
            @NotNull Function<MatchResult, Match> match
    ) {
        super(id);
        this.pattern = Pattern.compile("(\\\\?)(?:%s)".formatted(pattern));
        this.match = match;
    }

    @Override
    public void match(@NotNull MatchContext context) {
        final var matcher = pattern.matcher(context.stringifiedText);

        while (matcher.find())
            context.putMatch(match.apply(matcher.toMatchResult()), matcher.start());
    }
}
