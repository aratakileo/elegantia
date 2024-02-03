package io.github.aratakileo.elegantia.gui.config;

import io.github.aratakileo.elegantia.gui.screen.AbstractScreen;
import io.github.aratakileo.elegantia.gui.widget.Button;
import io.github.aratakileo.elegantia.util.Rect2i;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

public interface ConfigScreenMaker {
    static @Nullable AbstractScreen makeFor(@NotNull Class<? extends Config> configClass) {
        final var configInfo = Config.getConfigInfo(configClass);

        if (Objects.isNull(configInfo)) return null;

        final var modName = FabricLoader.getInstance()
                .getModContainer(configInfo.modId())
                .get()
                .getMetadata()
                .getName();

        final var screenTitle = Component.literal(modName + " settings");

        return new AbstractScreen(screenTitle) {
            @Override
            protected void init() {
                var y = getContentY();

                for (final var field: configInfo.fields()) {
                    final var fieldAnnotation = field.getAnnotation(ConfigField.class);
                    final var fieldName = field.getName();
                    final var configInstance = configInfo.configInstance();


                    final var buttonStateText = getStateText(configInstance.getBooleanFieldValue(fieldName));
                    final var button = new Button.Builder()
                            .setMessage(Component.literal(fieldAnnotation.title() + ": " + buttonStateText))
                            .setBounds(new Rect2i(getCenterHorizontal(), y, 0, 0))
                            .setOnClickListener((btn, byUser) -> {
                                configInstance.setFieldValue(fieldName, !configInstance.getBooleanFieldValue(fieldName));
                                ((Button)btn).setMessage(Component.literal(fieldAnnotation.title() + ": " + getStateText(configInstance.getBooleanFieldValue(fieldName))), 5);
                                Config.save(configInstance, new File("config/" + configInfo.modId() + ".json"));
                                return true;
                                })
                            .build();

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
                Config.save(configInfo.configInstance(), new File("config/" + configInfo.modId() + ".json"));
                super.onClose();
            }
        };
    }

    private static @NotNull String getStateText(boolean state) {
        return "§l" + (state ? "§2" : "§c") + (state ? "Enabled" : "Disabled");
    }
}
