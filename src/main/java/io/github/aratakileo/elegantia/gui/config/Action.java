package io.github.aratakileo.elegantia.gui.config;

import io.github.aratakileo.elegantia.util.Strings;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class Action {
    private final String translationKey, actionName;
    private final Runnable onClick;
    private final @Nullable String triggeredBy;

    public Action(
            @Nullable String actionName,
            @NotNull String translationKey,
            @NotNull Runnable onClick,
            @Nullable String triggeredBy
    ) {
        this.translationKey = translationKey;
        this.actionName = actionName;
        this.onClick = onClick;
        this.triggeredBy = triggeredBy;
    }

    public @NotNull Component getDescription(@NotNull String modId) {
        return Component.literal(Strings.requireReturnNotAsArgument(
                "%s.config.entry.%s.description".formatted(modId, translationKey),
                Language.getInstance()::getOrDefault,
                ""
        ));
    }

    public @NotNull Component getTitle(@NotNull String modId) {
        final var fullTranslationKey = "%s.config.entry.%s.title".formatted(modId, translationKey);

        if (Objects.isNull(actionName))
            return Component.translatable(fullTranslationKey);

        return Component.literal(Strings.requireReturnNotAsArgument(
                fullTranslationKey,
                Language.getInstance()::getOrDefault,
                "Action@" + actionName
        ));
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public @Nullable String getTriggeredBy() {
        return triggeredBy;
    }

    public boolean isTriggered() {
        return Objects.nonNull(triggeredBy) && !triggeredBy.isEmpty();
    }
}
