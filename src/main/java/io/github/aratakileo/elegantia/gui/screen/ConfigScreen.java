package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.gui.config.Trigger;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.util.Strings;
import io.github.aratakileo.elegantia.util.WidgetPositioner;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigScreen extends AbstractScreen {
    private final Config.ConfigInfo configInfo;
    private HashMap<String, List<String>> triggeredFields;
    private HashMap<String, Button> fieldToButton;

    protected ConfigScreen(
            @NotNull Config.ConfigInfo configInfo,
            @Nullable Screen parent
    ) {
        this(
                configInfo,
                ModInfo.getName(configInfo.modId()).orElse("Unknown"),
                parent
        );
    }

    protected ConfigScreen(
            @NotNull Config.ConfigInfo configInfo,
            @NotNull String modName,
            @Nullable Screen parent
    ) {
        super(Component.translatable("elegantia.gui.config.title", modName), parent);
        this.configInfo = configInfo;
    }

    @Override
    protected void init() {
        triggeredFields = new HashMap<>();
        fieldToButton = new HashMap<>();

        final var configInstance = configInfo.instance();
        var y = getContentY();

        for (final var field: configInfo.fields()) {
            final var fieldName = field.getName();
            final var fieldAnnotation = field.getAnnotation(ConfigField.class);
            final var triggeredBy = fieldAnnotation.triggeredBy();
            final var isTriggered = !triggeredBy.isEmpty();
            final var descriptionTranslationKey = Strings.doesNotMeetCondition(
                    fieldAnnotation.translationKey(),
                    String::isBlank,
                    Strings.camelToSnake(fieldName)
            );

            final var button = new Button(
                    WidgetPositioner.ofMessageContent(getBooleanButtonMessage(
                            field,
                            configInfo
                    )).setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL)
                            .setPadding(5)
                            .getNewBounds()
                            .moveIpY(y),
                    getBooleanButtonMessage(field, configInfo)
            );
            button.setOnClickListener((btn, byUser) -> {
                configInstance.invertBooleanFieldValue(fieldName);
                btn.setMessage(getBooleanButtonMessage(field, configInfo));
                btn.setBounds(
                        WidgetPositioner.ofMessageContent(btn.getMessage())
                                .setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL)
                                .setPadding(5)
                                .getNewBounds(btn.getBounds())
                );

                if (field.isAnnotationPresent(Trigger.class))
                    for (
                            final var triggeredFieldName:
                            triggeredFields.get(field.getAnnotation(Trigger.class).value())
                    ) {
                        final var triggeredButton = fieldToButton.get(triggeredFieldName);
                        final var triggerValue = configInstance.getBooleanFieldValue(fieldName);

                        if (!triggerValue && configInstance.getBooleanFieldValue(triggeredFieldName))
                            triggeredButton.onClick(false);

                        triggeredButton.isVisible = triggerValue;
                    }

                return true;
            });
            button.setTooltip(Strings.requireReturnNotAsArgument(
                    "%s.config.entry.%s.description".formatted(configInfo.modId(), descriptionTranslationKey),
                    Language.getInstance()::getOrDefault,
                    ""
            ));
            button.isVisible = !isTriggered
                    || configInstance.getBooleanFieldValue(configInfo.triggeredFields().get(triggeredBy));

            fieldToButton.put(fieldName, button);

            if(isTriggered) {
                if (!triggeredFields.containsKey(triggeredBy))
                    triggeredFields.put(triggeredBy, new ArrayList<>());

                triggeredFields.get(triggeredBy).add(fieldName);
            }

            addRenderableWidget(button);

            y += 20;
        }

        final var quitButtonMessage = Component.translatable("elegantia.gui.config.button.save_and_quit");

        final var quitButton = new Button(
                WidgetPositioner.ofMessageContent(quitButtonMessage)
                        .setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL | WidgetPositioner.GRAVITY_BOTTOM)
                        .setPadding(5)
                        .setMarginBottom(10)
                        .getNewBounds(),
                quitButtonMessage
        );
        quitButton.setOnClickListener((btn, byUser) -> {
            configInstance.save();
            onClose();

            return true;
        });

        addRenderableWidget(quitButton);
    }

    protected static @NotNull Component getBooleanButtonMessage(
            @NotNull Field field,
            @NotNull Config.ConfigInfo configInfo
    ) {
        final var state = configInfo.instance().getBooleanFieldValue(field.getName());
        final var fieldName = field.getName();

        final var translationKey = Strings.doesNotMeetCondition(
                field.getAnnotation(ConfigField.class).translationKey(),
                String::isBlank,
                Strings.camelToSnake(fieldName)
        );
        final var buttonMessageText = Strings.requireReturnNotAsArgument(
                "%s.config.entry.%s.title".formatted(configInfo.modId(), translationKey),
                Language.getInstance()::getOrDefault,
                fieldName
        );

        return Component.literal(buttonMessageText + ": §l" + (state ? "§2" : "§c")
                + Language.getInstance().getOrDefault("elegantia.gui.state." + (state ? "enabled" : "disabled"))
        );
    }

    public static @Nullable ConfigScreen of(@NotNull Class<? extends Config> configClass) {
        return of(configClass, getCurrentScreen());
    }

    public static @Nullable ConfigScreen of(@NotNull Class<? extends Config> configClass, @Nullable Screen parent) {
        final var configInfo = Config.getConfigInfo(configClass);

        if (Objects.isNull(configInfo)) return null;

        return new ConfigScreen(configInfo, parent);
    }
}
