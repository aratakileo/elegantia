package io.github.aratakileo.elegantia.gui.screen;

import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.config.ConfigField;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screens.Screen;
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
        super(Component.literal(
                FabricLoader.getInstance()
                        .getModContainer(configInfo.modId())
                        .get()
                        .getMetadata()
                        .getName() + " settings"
                ),
                parent
        );
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
            button.setTooltip(field.getAnnotation(ConfigField.class).description());

            addRenderableWidget(button);

            y += 20;
        }

        addRenderableWidget(
                new Button.Builder()
                        .setMessage(Component.literal("Save & Quit"))
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
        final var annotationTitle = field.getAnnotation(ConfigField.class).title();
        final var state = configInfo.instance().getBooleanFieldValue(field.getName());

        return Component.literal(
                (annotationTitle.isBlank() ? field.getName() : annotationTitle)
                        + ": §l"
                        + (state ? "§2" : "§c")
                        + (state ? "Enabled" : "Disabled")
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
