package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.Rect2i;
import io.github.aratakileo.elegantia.util.Strings;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Objects;

public class ConfigScreen extends AbstractScreen {
    private final Config.ConfigInfo configInfo;

    protected ConfigScreen(
            @NotNull Config.ConfigInfo configInfo,
            @Nullable Screen parent
    ) {
        this(
                configInfo,
                FabricLoader.getInstance()
                        .getModContainer(configInfo.modId())
                        .get()
                        .getMetadata()
                        .getName(),
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
        var y = getContentY();

        for (final var field: configInfo.fields()) {
            final var fieldName = field.getName();

            final var button = new Button.Builder()
                    .setMessage(getBooleanButtonMessage(field, configInfo))
                    .setBounds(new Rect2i(getCenterHorizontal(), y, 0, 0))
                    .setOnClickListener((btn, byUser) -> {
                        configInfo.instance().invertBooleanFieldValue(fieldName);
                        ((Button)btn).setMessage(getBooleanButtonMessage(field, configInfo), 5);
                        return true;
                    })
                    .build();

            final var descriptionTranslationKey = Strings.doesNoMeetCondition(
                    field.getAnnotation(ConfigField.class).translationKey(),
                    String::isBlank,
                    Strings.camelToSnake(fieldName)
            );

            button.setTooltip(Strings.requireReturnNotAsArgument(
                    configInfo.modId() + ".config.entry." + descriptionTranslationKey + ".description",
                    Language.getInstance()::getOrDefault,
                    ""
            ));

            addRenderableWidget(button);

            y += 20;
        }

        addRenderableWidget(
                new Button.Builder()
                        .setMessage(Component.translatable("elegantia.gui.config.button.save_and_quit"))
                        .setBounds(new Rect2i(getCenterHorizontal(), height - 20, 0, 0))
                        .setHasTopGravity(false)
                        .setOnClickListener((btn, byUser) -> {
                            onClose();
                            return true;
                        })
                        .build()
        );
    }

    @Override
    public void onClose() {
        configInfo.instance().save();
        super.onClose();
    }

    protected static @NotNull Component getBooleanButtonMessage(
            @NotNull Field field,
            @NotNull Config.ConfigInfo configInfo
    ) {
        final var state = configInfo.instance().getBooleanFieldValue(field.getName());
        final var fieldName = field.getName();

        final var translationKey = Strings.doesNoMeetCondition(
                field.getAnnotation(ConfigField.class).translationKey(),
                String::isBlank,
                Strings.camelToSnake(fieldName)
        );
        final var buttonMessageText = Strings.requireReturnNotAsArgument(
                configInfo.modId() + ".config.entry." + translationKey + ".title",
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
