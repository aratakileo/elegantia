package io.github.aratakileo.elegantia.common.environment;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface ModProfile {
    @NotNull String id();

    default @NotNull Origin origin() {
        return Origin.from(id());
    }

    @NotNull String name();
    @NotNull String description();
    @NotNull Collection<String> licenses();
    @NotNull String versionName();
    @NotNull Collection<Person> authors();
    @NotNull Collection<Person> contributors();
    @NotNull Contacts contacts();
}
