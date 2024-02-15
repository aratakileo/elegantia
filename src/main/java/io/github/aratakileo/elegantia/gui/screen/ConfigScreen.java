package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.EntryInfo;
import io.github.aratakileo.elegantia.gui.widget.AbstractWidget;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.ModInfo;
import io.github.aratakileo.elegantia.gui.WidgetBoundsBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ConfigScreen extends AbstractScreen {
    private final List<Pair<AbstractWidget, String>> entryWidgets = new ArrayList<>();
    private final Config.ConfigInfo configInfo;
    private HashMap<String, List<String>> triggeredFieldEntries;
    private HashMap<String, Button> entryFieldToButton;

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

    protected void addConfigFieldWidget(@NotNull Button button, @Nullable String triggeredBy) {
        entryWidgets.add(new Pair<>(button, triggeredBy));
        addRenderableWidget(button);
    }

    protected void applyWidgetsVisibility(@NotNull Config configInstance) {
        var y = getContentY();

        for (final var widgetEntry: entryWidgets) {
            final var triggeredBy = widgetEntry.getB();
            final var shouldBeShown = Objects.isNull(triggeredBy) || configInstance.getTriggerValue(triggeredBy);
            final var widget = widgetEntry.getA();

            widget.isVisible = shouldBeShown;

            if (!shouldBeShown) continue;

            widget.setY(y);
            y += widget.getHeight() + 2;
        }
    }

    @Override
    protected void init() {
        triggeredFieldEntries = new HashMap<>();
        entryFieldToButton = new HashMap<>();

        final var configInstance = configInfo.instance();
        final var modId = configInfo.modId();

        configInfo.entries().forEach(entryInfo -> {
            final var message = getEntryMessage(entryInfo);
            final var triggeredBy = entryInfo.getTriggeredBy();

            final var entryButton = new Button(
                    WidgetBoundsBuilder.ofMessageContent(message)
                            .setGravity(WidgetBoundsBuilder.GRAVITY_CENTER_HORIZONTAL)
                            .setPadding(5)
                            .build(),
                    message
            );
            entryButton.setTooltip(entryInfo.getDescriptionComponent(modId));

            addConfigFieldWidget(entryButton, triggeredBy);

            if (entryInfo.getType().isAction()) {
                entryButton.setOnClickListener(((button, byUser) -> {
                    entryInfo.execute();
                    return true;
                }));
                return;
            }

            final var entryName = Objects.requireNonNull(entryInfo.getName());

            entryButton.setOnClickListener((button, byUser) -> {
                configInstance.invertBooleanFieldValue(entryName);
                button.setMessage(getBooleanEntryMessage(entryInfo));
                button.setBounds(
                        WidgetBoundsBuilder.ofMessageContent(button.getMessage())
                                .setGravity(WidgetBoundsBuilder.GRAVITY_CENTER_HORIZONTAL)
                                .setPadding(5)
                                .setInitialBounds(button.getBounds())
                                .build()
                );

                if (!entryInfo.isTrigger()) return true;

                for (final var triggeredFieldName : triggeredFieldEntries.get(entryInfo.getTriggerName()))
                    if (configInstance.getBooleanFieldValue(triggeredFieldName))
                        entryFieldToButton.get(triggeredFieldName).onClick(false);

                applyWidgetsVisibility(configInstance);

                return true;
            });

            entryFieldToButton.put(entryName, entryButton);

            if (Objects.nonNull(triggeredBy)) {
                if (!triggeredFieldEntries.containsKey(triggeredBy))
                    triggeredFieldEntries.put(triggeredBy, new ArrayList<>());

                triggeredFieldEntries.get(triggeredBy).add(entryName);
            }
        });

        applyWidgetsVisibility(configInstance);

        final var quitButtonMessage = Component.translatable("elegantia.gui.config.button.save_and_quit");

        final var quitButton = new Button(
                WidgetBoundsBuilder.ofMessageContent(quitButtonMessage)
                        .setGravity(WidgetBoundsBuilder.GRAVITY_CENTER_HORIZONTAL | WidgetBoundsBuilder.GRAVITY_BOTTOM)
                        .setPadding(5)
                        .setMarginBottom(10)
                        .build(),
                quitButtonMessage
        );
        quitButton.setOnClickListener((btn, byUser) -> {
            configInstance.save();
            onClose();

            return true;
        });

        addRenderableWidget(quitButton);
    }

    protected @NotNull Component getEntryMessage(@NotNull EntryInfo entryInfo) {
        if (entryInfo.getType().isBoolean()) return getBooleanEntryMessage(entryInfo);

        return Component.literal(entryInfo.getTitle(configInfo.modId()));
    }

    protected @NotNull Component getBooleanEntryMessage(@NotNull EntryInfo entryInfo) {
        final var state = configInfo.instance().getBooleanFieldValue(Objects.requireNonNull(entryInfo.getName()));

        return Component.literal("%s: §l%s%s".formatted(
                entryInfo.getTitle(configInfo.modId()),
                (state ? "§2" : "§c"),
                Language.getInstance().getOrDefault("elegantia.gui.state." + (state ? "enabled" : "disabled"))
        ));
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
