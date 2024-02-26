package io.github.aratakileo.elegantia.util;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.exception.NoSuchModException;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.Person;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;

public abstract class ModInfo {
    public abstract @NotNull String getId();
    public abstract @NotNull String getVersion();
    public abstract @NotNull String getName();
    public abstract @NotNull String getDescription();
    public abstract @NotNull Collection<String> getAuthors();
    public abstract @NotNull Collection<String> getContributors();
    public abstract @NotNull Optional<Map<String, String>> getUrls();
    public abstract @NotNull Environment getEnvironment();
    public abstract @NotNull List<Path> getRootPaths();

    public @NotNull Optional<Path> findPath(@NotNull String relativePath) {
        for (final var rootPath : getRootPaths()) {
            final var path = rootPath.resolve(relativePath.replace("/", rootPath.getFileSystem().getSeparator()));
            if (Files.exists(path)) return Optional.of(path);
        }

        return Optional.empty();
    }

    public @NotNull Optional<String> getUrl(@NotNull String key) {
        return getUrls().map(map -> map.get(key));
    }

    public @NotNull Optional<String> getSourcesUrl() {
        return getUrl("sources");
    }

    public @NotNull Optional<String> getIssuesUrl() {
        return getUrl("issues");
    }

    public @NotNull Optional<String> getHomepageUrl() {
        return getUrl("homepage");
    }

    @SuppressWarnings("unchecked")
    public void setConfigScreenGetter(@NotNull Function<Screen, Screen> configScreenGetter) {
        if (!isModLoaded("modmenu")) return;

        /*
         * This check is necessary to avoid crashing when launching the mod by Forge/Neoforge in situations:
         *  - Sinytra Connector is installed without Forgified Fabric API
         *  - Sinytra Connector and Connector Extras are installed
         *
         * NOTE: Mod with id `fabric_api` is Forgified Fabric API
         */
        if (
                isModLoaded("connectormod")
                        && !isModLoaded("fabric_api")
                        || isModLoaded("connectorextras")
        ) return;

        try {
            /*
             * Adding configuration screens is necessary in this way, due to the fact
             * that the `ModMenuApi#getProvidedConfigScreenFactories` interface works every other time
             */
            final var field = ModMenu.class.getDeclaredField("configScreenFactories");
            field.setAccessible(true);

            final var configScreenFactories = (Map<String, ConfigScreenFactory<?>>) field.get(null);
            configScreenFactories.put(getId(), configScreenGetter::apply);

            field.set(null, configScreenFactories);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            Elegantia.LOGGER.error(
                    "Something went wrong while trying to set config screen getter for mod id `" + getId() + '`',
                    e
            );
        }
    }

    public static @NotNull Optional<ModInfo> get(@NotNull String modId) {
        final var modContainer = FabricLoader.getInstance()
                .getModContainer(modId)
                .orElse(null);

        if (Objects.isNull(modContainer))
            return Optional.empty();

        final var modMetadata = modContainer.getMetadata();

        return Optional.of(new ModInfo() {
            @Override
            public @NotNull String getId() {
                return modMetadata.getId();
            }

            @Override
            public @NotNull String getVersion() {
                return modMetadata.getVersion().getFriendlyString();
            }

            @Override
            public @NotNull String getName() {
                return modMetadata.getName();
            }

            @Override
            public @NotNull String getDescription() {
                return modMetadata.getDescription();
            }

            @Override
            public @NotNull Collection<String> getAuthors() {
                return modMetadata.getAuthors().stream().map(Person::getName).toList();
            }

            @Override
            public @NotNull Collection<String> getContributors() {
                return modMetadata.getContributors().stream().map(Person::getName).toList();
            }

            @Override
            public @NotNull Optional<Map<String, String>> getUrls() {
                return Optional.ofNullable(modMetadata.getContact().asMap());
            }

            @Override
            public @NotNull Environment getEnvironment() {
                return switch (modMetadata.getEnvironment()) {
                    case CLIENT -> Environment.CLIENT;
                    case SERVER -> Environment.SERVER;
                    case UNIVERSAL -> Environment.BOTH;
                };
            }

            @Override
            public @NotNull List<Path> getRootPaths() {
                return modContainer.getRootPaths();
            }
        });
    }

    public static @NotNull Optional<String> getVersion(@NotNull String modId) {
        return get(modId).map(ModInfo::getVersion);
    }

    public static @NotNull Optional<String> getName(@NotNull String modId) {
        return get(modId).map(ModInfo::getName);
    }

    public static @NotNull Optional<String> getDescription(@NotNull String modId) {
        return get(modId).map(ModInfo::getDescription);
    }

    public static @NotNull Optional<Collection<String>> getAuthors(@NotNull String modId) {
        return get(modId).map(ModInfo::getAuthors);
    }

    public static @NotNull Optional<Collection<String>> getContributors(@NotNull String modId) {
        return get(modId).map(ModInfo::getContributors);
    }

    public static @NotNull Optional<Map<String, String>> getUrls(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getUrls);
    }

    public static @NotNull Optional<List<Path>> getRootPaths(@NotNull String modId) {
        return get(modId).map(ModInfo::getRootPaths);
    }

    public static @NotNull Optional<Path> findPath(@NotNull String modId, @NotNull String relativePath) {
        return get(modId).flatMap(modInfo -> modInfo.findPath(relativePath));
    }

    public static @NotNull Optional<String> getUrl(@NotNull String modId, @NotNull String key) {
        return get(modId).flatMap(modInfo -> modInfo.getUrl(key));
    }

    public static @NotNull Optional<String> getSourcesUrl(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getSourcesUrl);
    }

    public static @NotNull Optional<String> getIssuesUrl(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getIssuesUrl);
    }

    public static @NotNull Optional<String> getHomepageUrl(@NotNull String modId) {
        return get(modId).flatMap(ModInfo::getHomepageUrl);
    }

    public static @NotNull Optional<Environment> getEnvironment(@NotNull String modId) {
        return get(modId).map(ModInfo::getEnvironment);
    }

    public static void setConfigScreenGetter(
            @NotNull String modId,
            @NotNull Function<Screen, Screen> configScreenGetter
    ) {
        if (!isModLoaded("modmenu")) return;

        if (!isModLoaded(modId)) {
            Elegantia.LOGGER.error(
                    "Failed to set config screen getter for mod id `"
                            + modId
                            + "`, because mod does not exist"
            );
            return;
        }

        get(modId).orElseThrow().setConfigScreenGetter(configScreenGetter);
    }

    public static boolean isModLoaded(@NotNull String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static void throwIfModIsNotLoaded(@NotNull String modId) {
        if (!isModLoaded(modId)) throw new NoSuchModException("id=" + modId);
    }

    public enum Environment {
        CLIENT,
        SERVER,
        BOTH;
    }
}
