package io.github.aratakileo.elegantia.util;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.gui.config.Config;
import io.github.aratakileo.elegantia.gui.screen.ConfigScreen;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ModInfo {
    @NotNull String getVersion();
    @NotNull String getName();
    @NotNull Optional<String> getSourcesUrl();

    static @NotNull Optional<ModInfo> get(@NotNull String modId) {
        final var modMetadata = FabricLoader.getInstance()
                .getModContainer(modId)
                .map(ModContainer::getMetadata)
                .orElse(null);

        if (Objects.isNull(modMetadata))
            return Optional.empty();

        return Optional.of(new ModInfo() {
            @Override
            public @NotNull String getVersion() {
                return modMetadata.getVersion().getFriendlyString();
            }

            @Override
            public @NotNull String getName() {
                return modMetadata.getName();
            }

            @Override
            public @NotNull Optional<String> getSourcesUrl() {
                return modMetadata.getContact().get("sources");
            }
        });
    }

    static @NotNull Optional<String> getName(@NotNull String modId) {
        return get(modId).map(ModInfo::getName);
    }

    static @NotNull Optional<String> getVersion(@NotNull String modId) {
        return get(modId).map(ModInfo::getVersion);
    }

    static @NotNull Optional<String> getSourcesUrl(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getSourcesUrl);
    }

    @SuppressWarnings("unchecked")
    static void setConfigScreenGetter(@NotNull String modId, @NotNull Function<Screen, Screen> configScreenGetter) {
        if (!isModLoaded("modmenu")) return;

        if (!isModLoaded(modId)) {
            Elegantia.LOGGER.error(
                    "Failed to set config screen getter for mod id `"
                            + modId
                            + "`, because mod does not exist"
            );
            return;
        }

        try {
            /*
             * Adding configuration screens is necessary in this way, due to the fact
             * that the `ModMenuApi#getProvidedConfigScreenFactories` interface works every other time
             */
            final var field = ModMenu.class.getDeclaredField("configScreenFactories");
            field.setAccessible(true);

            final var configScreenFactories = (Map<String, ConfigScreenFactory<?>>) field.get(null);
            configScreenFactories.put(modId, configScreenGetter::apply);

            field.set(null, configScreenFactories);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Elegantia.LOGGER.error(
                    "Something went wrong while trying to set config screen getter for mod id `" + modId + '`',
                    e
            );
        }
    }

    static boolean isModLoaded(@NotNull String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }
}
