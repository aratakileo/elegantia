package io.github.aratakileo.elegantia.fabric.profile;

import io.github.aratakileo.elegantia.common.environment.Person;
import io.github.aratakileo.elegantia.common.environment.Contacts;
import io.github.aratakileo.elegantia.common.environment.ModProfile;
import net.fabricmc.loader.api.ModContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

public final class FabricModProfileImpl implements ModProfile {
    private final Collection<String> licenses;

    private final Collection<Person> authors, contributors;

    private final String versionName, description, name, id;

    private final Contacts contacts;

    private FabricModProfileImpl(@NotNull ModContainer src) {
        final var meta = src.getMetadata();

        id = meta.getId();
        name = meta.getName();
        description = meta.getDescription();
        versionName = meta.getVersion().getFriendlyString();
        licenses = meta.getLicense();

        authors = meta.getAuthors()
                .stream()
                .map(person -> Person.create(person.getName(), Contacts.create(person.getContact().asMap())))
                .collect(Collectors.toSet());

        contributors = meta.getContributors()
                .stream()
                .map(person -> Person.create(person.getName(), Contacts.create(person.getContact().asMap())))
                .collect(Collectors.toSet());

        contacts = Contacts.create(meta.getContact().asMap());
    }

    public static @NotNull FabricModProfileImpl from(@NotNull ModContainer src) {
        return new FabricModProfileImpl(src);
    }

    @Override
    public @NotNull String id() {
        return id;
    }

    @Override
    public @NotNull String name() {
        return name;
    }

    @Override
    public @NotNull String description() {
        return description;
    }

    @Override
    public @NotNull Collection<String> licenses() {
        return licenses;
    }

    @Override
    public @NotNull String versionName() {
        return versionName;
    }

    @Override
    public @NotNull Collection<Person> authors() {
        return authors;
    }

    @Override
    public @NotNull Collection<Person> contributors() {
        return contributors;
    }

    @Override
    public @NotNull Contacts contacts() {
        return contacts;
    }
}
