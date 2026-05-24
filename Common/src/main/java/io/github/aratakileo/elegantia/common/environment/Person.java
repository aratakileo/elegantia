package io.github.aratakileo.elegantia.common.environment;

import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.NotNull;

public final class Person {
    private final String name;
    private final Contacts contacts;

    private Person(@NotNull String name, @NotNull Contacts contacts) {
        this.name = name;
        this.contacts = contacts;
    }

    public @NotNull String name() {
        return name;
    }

    public @NotNull Contacts contacts() {
        return contacts;
    }

    public static @NotNull Person create(@NotNull String name) {
        return new Person(name, Contacts.EMPTY);
    }

    public static @NotNull Person create(@NotNull String name, @NotNull Contacts contacts) {
        return new Person(name, contacts);
    }

    @Override
    public String toString() {
        return Strings.format("{}({})", Person.class.getSimpleName(), name);
    }
}
