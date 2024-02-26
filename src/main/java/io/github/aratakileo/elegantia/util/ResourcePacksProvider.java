package io.github.aratakileo.elegantia.util;

import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

public final class ResourcePacksProvider {
    private final static HashMap<String, Pack> CLIENT_RESOURCE_PACKS = new HashMap<>();
    private final static HashMap<String, Pack> SERVER_RESOURCE_PACKS = new HashMap<>();
    private final static ArrayList<String> DEFAULT_ENABLED_RESOURCE_PACK_NAMES = new ArrayList<>();

    private final ResourceLocation packLocation;
    private boolean autoCompatibilityCompliance = false, isDefaultEnabled = true;
    private @NotNull String packsDirPath = "resourcepacks";
    private @Nullable Component displayName = null;

    private ResourcePacksProvider(@NotNull ResourceLocation packLocation) {
        ModInfo.throwIfModIsNotLoaded(packLocation.getNamespace());

        this.packLocation = packLocation;
    }

    public static @NotNull ResourcePacksProvider defineBuiltin(@NotNull ResourceLocation resourceLocation) {
        return new ResourcePacksProvider(resourceLocation);
    }

    public static @NotNull ResourcePacksProvider defineBuiltin(
            @NotNull String modId,
            @NotNull String packDirPath
    ) {
        return new ResourcePacksProvider(new ResourceLocation(modId, packDirPath));
    }

    public @NotNull ResourcePacksProvider setAutoCompatibilityCompliance(boolean autoCompatibilityCompliance) {
        this.autoCompatibilityCompliance = autoCompatibilityCompliance;
        return this;
    }

    public @NotNull ResourcePacksProvider setDefaultEnabled(boolean defaultEnabled) {
        isDefaultEnabled = defaultEnabled;
        return this;
    }

    public @NotNull ResourcePacksProvider setPacksDirPath(@NotNull String packsDirPath) {
        this.packsDirPath = packsDirPath;
        return this;
    }

    public @NotNull ResourcePacksProvider setDisplayName(@NotNull Component displayName) {
        this.displayName = displayName;
        return this;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean register() {
        final var optionalResourcePacksPath = ModInfo.findPath(
                packLocation.getNamespace(),
                packsDirPath
        );

        if (optionalResourcePacksPath.isEmpty()) return false;

        final var resourcePackPath = optionalResourcePacksPath.get()
                .resolve(packLocation.getPath())
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(resourcePackPath)) return false;

        final var name = packLocation.toString();
        final var finishedDisplayName = Objects.nonNull(displayName) ? displayName : Component.translatable(
                "%s.resourcepack.%s.title".formatted(
                        packLocation.getNamespace(),
                        packLocation.getPath().replaceAll("[/\\\\]+", ".")
                )
        );

        var result = Platform.getEnvironment().isClient() && registerBuiltin(
                PackType.CLIENT_RESOURCES,
                name,
                finishedDisplayName,
                resourcePackPath,
                autoCompatibilityCompliance
        );

        result |= registerBuiltin(
                PackType.SERVER_DATA,
                name,
                finishedDisplayName,
                resourcePackPath,
                autoCompatibilityCompliance
        );

        if (result && isDefaultEnabled)
            DEFAULT_ENABLED_RESOURCE_PACK_NAMES.add(name);

        return result;
    }

    public static void consumeBuiltinPacks(@NotNull PackType type, Consumer<Pack> packConsumer) {
        final var builtinPacks = type == PackType.CLIENT_RESOURCES ? CLIENT_RESOURCE_PACKS : SERVER_RESOURCE_PACKS;

        builtinPacks.values().forEach(packConsumer);
    }

    public static @NotNull List<String> getDefaultEnabledResourcePackNames() {
        return DEFAULT_ENABLED_RESOURCE_PACK_NAMES;
    }

    private static boolean registerBuiltin(
            @NotNull PackType packType,
            @NotNull String name,
            @NotNull Component displayName,
            @NotNull Path resourcePackPath,
            boolean autoCompatibilityCompliance
    ) {
        final var pack = createBuiltinPack(
                packType,
                name,
                path -> new PathPackResources(path, resourcePackPath, false),
                displayName,
                autoCompatibilityCompliance
        );

        if (Objects.isNull(pack)) return false;

        if (packType == PackType.CLIENT_RESOURCES)
            CLIENT_RESOURCE_PACKS.put(pack.getId(), pack);
        else
            SERVER_RESOURCE_PACKS.put(pack.getId(), pack);

        return true;
    }

    private static @Nullable Pack createBuiltinPack(
            @NotNull PackType packType,
            @NotNull String name,
            @NotNull Pack.ResourcesSupplier resourcesSupplier,
            @NotNull Component displayName,
            boolean autoCompatibilityCompliance
    ) {
        var info = Pack.readPackInfo(name, resourcesSupplier);

        if (Objects.isNull(info))
            return null;

        if (autoCompatibilityCompliance)
            info = new Pack.Info(
                    info.description(),
                    SharedConstants.getCurrentVersion().getPackVersion(packType),
                    info.requestedFeatures()
            );

        return Pack.create(
                name,
                displayName,
                false,
                resourcesSupplier,
                info,
                packType,
                Pack.Position.TOP,
                false,
                PackSource.BUILT_IN
        );
    }
}
