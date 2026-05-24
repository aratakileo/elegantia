package io.github.aratakileo.elegantia.forge.profile;

import io.github.aratakileo.elegantia.common.environment.ContactKey;
import io.github.aratakileo.elegantia.common.environment.Contacts;
import io.github.aratakileo.elegantia.common.environment.ModProfile;
import io.github.aratakileo.elegantia.common.environment.Person;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.forgespi.language.IModInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ForgeModProfile implements ModProfile {
    private final Collection<String> licenses;

    private final Collection<Person> authors, contributors;

    private final String versionName, description, name, id;

    private final Contacts contacts;

    private ForgeModProfile(@NotNull ModContainer src) {
        final var meta = src.getModInfo();

        id = src.getModId();
        name = meta.getDisplayName();
        description = meta.getDescription();
        versionName = meta.getOwningFile().versionString();
        licenses = Set.of(meta.getOwningFile().getLicense());
        authors = getPersonsFromString(meta, "authors", "author");
        contributors = getPersonsFromString(meta, "credits", "contributors", "contributor");

        contacts = getContacts(meta);
    }

    public static @NotNull ForgeModProfile from(@NotNull ModContainer src) {
        return new ForgeModProfile(src);
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

    private static @NotNull Contacts getContacts(@NotNull IModInfo meta) {
        final var contactsMap = new HashMap<String, String>();

        meta.getModURL().ifPresent(url -> contactsMap.put(
                ContactKey.HOMEPAGE.lowerName(),
                url.toString()
        ));

        getConfigValue(meta, "discord").ifPresent(value -> contactsMap.put(
                ContactKey.DISCORD.lowerName(),
                String.valueOf(value)
        ));

        getConfigValue(meta, "sources").ifPresent(value -> contactsMap.put(
                ContactKey.SOURCES.lowerName(),
                String.valueOf(value)
        ));

        getAnyConfigValue(meta, "issueTrackerURL", "issues")
                .ifPresent(value -> {
                    final var link = String.valueOf(value);

                    contactsMap.put(ContactKey.ISSUES.lowerName(), link);
                    putResourcesFrom(contactsMap, link);
                });

        return Contacts.create(Collections.unmodifiableMap(contactsMap));
    }

    private static final Pattern ISSUES_PATTERN = Pattern.compile("/(-/)?issues(/.*)?$", Pattern.CASE_INSENSITIVE);

    private static void putResourcesFrom(@NotNull HashMap<String, String> contacts, @NotNull String issuesLink) {
        if (contacts.containsKey(ContactKey.SOURCES.lowerName())) return;

        String trimmedUrl = issuesLink.trim().replaceAll("/+$", "");

        final var matcher = ISSUES_PATTERN.matcher(trimmedUrl);

        if (matcher.find())
            contacts.put(ContactKey.SOURCES.lowerName(), trimmedUrl.substring(0, matcher.start()));
    }

    private static @NotNull @UnmodifiableView Collection<Person> getAuthors(@NotNull IModInfo meta) {
        return meta.getOwningFile().getMods().get(0).getOwningFile().getConfig().getConfigElement("authors").map(
                value -> ((Collection<String>)value).stream().map(Person::create).collect(Collectors.toSet())
        ).get();
    }

    private static @NotNull @UnmodifiableView Collection<Person> getPersonsFromString(
            @NotNull IModInfo meta,
            @NotNull String @NotNull... keys
    ) {
        final var persons = new HashSet<Person>();

        for (final var key: keys) {
            final var _persons = getPersonsFromString(meta, key);
            if (_persons != null) persons.addAll(_persons);
        }

        return Collections.unmodifiableCollection(persons);
    }

    private static @Nullable @UnmodifiableView Collection<Person> getPersonsFromString(
            @NotNull IModInfo meta,
            @NotNull String key
    ) {
        return getConfigValue(meta, key)
                .map(
                        value -> {
                            if (value instanceof Collection<?> collection)
                                return collection.stream()
                                        .map(String::valueOf)
                                        .map(String::trim)
                                        .map(Person::create)
                                        .collect(Collectors.toSet());

                            return Arrays.stream(value.toString().split("\\s*,\\s*"))
                                    .map(String::trim)
                                    .filter(person -> !person.isEmpty())
                                    .map(Person::create)
                                    .collect(Collectors.toSet());
                        }
                ).orElse(null);
    }

    private static @NotNull Optional<Object> getAnyConfigValue(
            @NotNull IModInfo meta,
            @NotNull String @NotNull... keys
    ) {
        return Arrays.stream(keys)
                .map(key -> getConfigValue(meta, key))
                .filter(Optional::isPresent)
                .findFirst()
                .map(Optional::get);
    }

    private static @NotNull Optional<Object> getConfigValue(@NotNull IModInfo meta, @NotNull String key) {
        final var rootConfig = meta.getOwningFile().getConfig();
        final var rootValue = rootConfig.getConfigElement(key);

        if (rootValue.isPresent()) return rootValue;

        final var modsList = rootConfig.getConfigList("mods");

        for (final var modConfig: modsList) {
            final var cfgModId = modConfig.getConfigElement("modId");

            if (cfgModId.isPresent() && cfgModId.get().equals(meta.getModId()))
                return modConfig.getConfigElement(key);
        }

        return Optional.empty();
    }
}
