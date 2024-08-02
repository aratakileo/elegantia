package io.github.aratakileo.elegantia.config;

import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.elegantia.util.Strings;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class EntryInfo {
    private final Type type;
    private final @Nullable String triggerName, triggeredBy, name, translationKey;
    private final @Nullable Runnable onClick;

    private EntryInfo(
            @NotNull Type type,
            @Nullable String name,
            @Nullable String triggerName,
            @Nullable String triggeredBy,
            @Nullable String translationKey,
            @Nullable Runnable onClick
    ) {
        this.type = type;
        this.triggerName = triggerName;
        this.triggeredBy = Objects.isNull(triggeredBy) || triggeredBy.isEmpty() ? null : triggeredBy;
        this.translationKey = translationKey;
        this.name = name;
        this.onClick = onClick;

        if ((Objects.isNull(name) || name.isBlank()) && (Objects.isNull(translationKey) || translationKey.isBlank()))
            throw new InvalidConfigEntryException(
                    "translationKey `%s` and name `%s` cannot have bad values at the same time. %s".formatted(
                            translationKey,
                            name,
                            "At least one of these parameters must have a full value!"
                    )
            );
    }

    public @NotNull Type getType() {
        return type;
    }

    public boolean isTrigger() {
        return Objects.nonNull(triggerName);
    }

    public @Nullable String getTriggerName() {
        return triggerName;
    }

    public @Nullable String getTriggeredBy() {
        return triggeredBy;
    }

    public @Nullable String getName() {
        return name;
    }

    private @NotNull String getTranslationKey(@NotNull Namespace namespace, @NotNull String postfix) {
        return "%s.config.entry.%s.%s".formatted(namespace.get(), Strings.doesNotMeetCondition(
                Objects.requireNonNullElse(translationKey, ""),
                String::isBlank,
                Strings.camelToSnake(Objects.requireNonNullElse(name, ""))
        ), postfix);
    }

    public @NotNull Component getDescriptionComponent(@NotNull Namespace namespace) {
        return Component.literal(Strings.requireReturnNotAsArgument(
                getTranslationKey(namespace, "description"),
                Language.getInstance()::getOrDefault,
                ""
        ));
    }

    public @NotNull String getTitle(@NotNull Namespace namespace) {
        final var fullTranslationKey = getTranslationKey(namespace, "title");

        return Objects.isNull(name) ? fullTranslationKey : Strings.requireReturnNotAsArgument(
                fullTranslationKey,
                Language.getInstance()::getOrDefault,
                "%s@%s".formatted(Strings.capitalize(getType().name().toLowerCase()), name)
        );
    }

    public void execute() {
        if (Objects.isNull(onClick)) return;

        onClick.run();
    }

    public static @NotNull EntryInfo action(
            @Nullable String name,
            @Nullable String triggeredBy,
            @Nullable String translationKey,
            @NotNull Runnable onClick,
            boolean isInfluential
    ) {
        return new EntryInfo(
                Type.ACTION,
                name,
                isInfluential ? "influential" : null,
                triggeredBy,
                translationKey,
                onClick
        );
    }

    public static @NotNull EntryInfo value(
            @NotNull Type type,
            @NotNull String name,
            @Nullable String triggerName,
            @Nullable String triggeredBy,
            @Nullable String translationKey
    ) {
        if (type.isAction())
            throw new InvalidConfigEntryException(
                    "Value entry cannot be an action! (name: %s, translationKey: %s)".formatted(name, translationKey)
            );

        return new EntryInfo(type, name, triggerName, triggeredBy, translationKey, null);
    }

    public enum Type {
        BOOLEAN,
        ACTION;

        public boolean isBoolean() {
            return this == BOOLEAN;
        }

        public boolean isAction() {
            return this == ACTION;
        }
    }

    public static class InvalidConfigEntryException extends RuntimeException {
        public InvalidConfigEntryException(@NotNull String message) {
            super(message);
        }
    }
}
