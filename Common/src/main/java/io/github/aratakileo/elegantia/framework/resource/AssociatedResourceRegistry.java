package io.github.aratakileo.elegantia.framework.resource;

import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.util.Exceptions;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class AssociatedResourceRegistry {
    private static final ConcurrentHashMap<Identifier, Object> ASSOCIATED;

    private AssociatedResourceRegistry() {}

    public static void associate(@NotNull Identifier id, @NotNull Object association) {
        if (Exceptions.throwOrLogIf(
                ASSOCIATED.containsKey(id),
                Origin.from(id),
                IllegalArgumentException::new, "resource `{}` already has association",
                id
        )) return;

        ASSOCIATED.put(id, association);
    }

    public static @NotNull Object unwrap(@NotNull Identifier id) {
        return Objects.requireNonNull(ASSOCIATED.get(id));
    }

    public static @NotNull Optional<Object> optional(@NotNull Identifier id) {
        return Optional.ofNullable(ASSOCIATED.get(id));
    }

    public static boolean has(@NotNull Identifier id) {
        return ASSOCIATED.containsKey(id);
    }

    static {
        ASSOCIATED = new ConcurrentHashMap<>();
    }
}
