package io.github.aratakileo.elegantia.client.text;

import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.BitSet;
import java.util.Optional;
import java.util.TreeMap;

public class MatchContext {
    public final String stringifiedText;
    public final Component text;
    private final TreeMap<Integer, Match> matched = new TreeMap<>();
    private final BitSet covered;

    private MatchContext(@NotNull String stringifiedText, @NotNull Component text) {
        this.stringifiedText = stringifiedText;
        this.text = text;
        this.covered = new BitSet(stringifiedText.length());
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean putMatch(@NotNull Match match, int start) {
        int end = start + match.value().length();

        if (covered.get(start) || covered.get(end)) return false;

        covered.set(start, end);
        matched.put(start, match);

        return true;
    }

    public @NotNull Optional<Match> getMatch(int index) {
        return Optional.ofNullable(matched.get(index));
    }

    public static @NotNull MatchContext of(@NotNull String stringifiedText) {
        return new MatchContext(stringifiedText, Component.literal(stringifiedText));
    }

    public static @NotNull MatchContext of(@NotNull Component text) {
        return new MatchContext(text.getString(), text);
    }
}
