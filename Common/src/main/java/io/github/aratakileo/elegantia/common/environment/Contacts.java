package io.github.aratakileo.elegantia.common.environment;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public final class Contacts {
    public static final @NotNull Contacts EMPTY = new Contacts(Collections.emptyMap());

    private final Map<String, String> map;

    private Contacts(@NotNull Map<String, String> map) {
        this.map = map;
    }

    public @NotNull Optional<String> optional(@NotNull ContactKey key) {
        return Optional.ofNullable(map.get(key.lowerName()));
    }

    public @NotNull Optional<String> optional(@NotNull String key) {
        return Optional.ofNullable(map.get(key));
    }

    public @NotNull String unwrap(@NotNull String key) {
        return Objects.requireNonNull(map.get(key));
    }

    public @NotNull String unwrap(@NotNull ContactKey key) {
        return Objects.requireNonNull(map.get(key.lowerName()));
    }

    public @NotNull @UnmodifiableView Collection<String> get(@NotNull ContactKey @NotNull... keys) {
        final var contacts = new HashSet<String>();

        for (final var key: keys)
            if (map.containsKey(key.lowerName()))
                contacts.add(map.get(key.lowerName()));

        return Collections.unmodifiableCollection(contacts);
    }

    public @NotNull @UnmodifiableView Collection<String> get(@NotNull String @NotNull... keys) {
        final var contacts = new HashSet<String>();

        for (final var key: keys)
            if (map.containsKey(key))
                contacts.add(map.get(key));

        return Collections.unmodifiableCollection(contacts);
    }

    public @NotNull Map<String, String> asMap() {
        return map;
    }

    public static @NotNull Contacts create(@NotNull Map<String, String> contacts) {
        if (contacts.isEmpty()) return EMPTY;
        return new Contacts(contacts);
    }
}
