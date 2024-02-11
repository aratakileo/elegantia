package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.gui.config.Trigger;
import io.github.aratakileo.elegantia.gui.widget.AbstractWidget;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.util.Strings;
import io.github.aratakileo.elegantia.util.WidgetPositioner;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigScreen extends AbstractScreen {
    private final List<Pair<AbstractWidget, String>> configFieldWidgets = new ArrayList<>();
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

    protected void addConfigFieldWidget(@NotNull Button button, String triggeredBy) {
        configFieldWidgets.add(new Pair<>(button, triggeredBy));
        addRenderableWidget(button);
    }

    protected void applyWidgetsVisibility(@NotNull Config configInstance) {
        var y = getContentY();

        for (final var widgetEntry: configFieldWidgets) {
            final var triggeredBy = widgetEntry.getB();
            final var shouldBeShown = triggeredBy.isEmpty() || configInstance.getTriggerValue(triggeredBy);
            final var widget = widgetEntry.getA();

            widget.isVisible = shouldBeShown;

            if (!shouldBeShown) continue;

            widget.setY(y);
            y += widget.getHeight() + 2;
        }
    }

    @Override
    protected void init() {
        triggeredFields = new HashMap<>();
        fieldToButton = new HashMap<>();

        final var configInstance = configInfo.instance();
        final var modId = configInfo.modId();

        for (final var field: configInfo.fields()) {
            final var fieldAnnotation = field.getAnnotation(ConfigField.class);
            final var triggeredBy = fieldAnnotation.triggeredBy();

            final var fieldName = field.getName();
            final var descriptionTranslationKey = Strings.doesNotMeetCondition(
                    fieldAnnotation.translationKey(),
                    String::isBlank,
                    Strings.camelToSnake(fieldName)
            );

            final var fieldButton = new Button(
                    WidgetPositioner.ofMessageContent(getBooleanButtonMessage(
                            field,
                            configInfo
                    )).setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL)
                            .setPadding(5)
                            .getNewBounds(),
                    getBooleanButtonMessage(field, configInfo)
            );
            fieldButton.setOnClickListener((button, byUser) -> {
                configInstance.invertBooleanFieldValue(fieldName);
                button.setMessage(getBooleanButtonMessage(field, configInfo));
                button.setBounds(
                        WidgetPositioner.ofMessageContent(button.getMessage())
                                .setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL)
                                .setPadding(5)
                                .getNewBoundsOf(button.getBounds())
                );

                if (!field.isAnnotationPresent(Trigger.class)) return true;

                for (final var triggeredFieldName : triggeredFields.get(field.getAnnotation(Trigger.class).value()))
                    if (configInstance.getBooleanFieldValue(triggeredFieldName))
                        fieldToButton.get(triggeredFieldName).onClick(false);

                applyWidgetsVisibility(configInstance);

                return true;
            });
            fieldButton.setTooltip(Strings.requireReturnNotAsArgument(
                    "%s.config.entry.%s.description".formatted(modId, descriptionTranslationKey),
                    Language.getInstance()::getOrDefault,
                    ""
            ));

            fieldToButton.put(fieldName, fieldButton);

            if (!triggeredBy.isEmpty()) {
                if (!triggeredFields.containsKey(triggeredBy))
                    triggeredFields.put(triggeredBy, new ArrayList<>());

                triggeredFields.get(triggeredBy).add(fieldName);
            }

            addConfigFieldWidget(fieldButton, triggeredBy);
        }

        for (final var action: configInfo.actions()) {
            final var actionTitle = action.getTitle(modId);

            final var actionButton = new Button(
                    WidgetPositioner.ofMessageContent(actionTitle)
                            .setGravity(WidgetPositioner.GRAVITY_CENTER_HORIZONTAL)
                            .setPadding(5)
                            .getNewBounds(),
                    actionTitle
            );
            actionButton.setOnClickListener((button, byUSer) -> {
                action.execute();
                return true;
            });
            actionButton.setTooltip(action.getDescription(modId));
            addConfigFieldWidget(actionButton, action.getTriggeredBy());
        }

        applyWidgetsVisibility(configInstance);

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
