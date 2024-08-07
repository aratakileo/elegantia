package io.github.aratakileo.elegantia.server;

import io.github.aratakileo.elegantia.core.Platform;
import io.github.aratakileo.elegantia.core.ModInfo;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.repository.KnownPack;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Consumer;

public final class ResourcePacksProvider {
    private final static HashMap<String, Pack> CLIENT_RESOURCE_PACKS = new HashMap<>();
    private final static HashMap<String, Pack> SERVER_RESOURCE_PACKS = new HashMap<>();
    private final static ArrayList<String> DEFAULT_ENABLED_RESOURCE_PACK_NAMES = new ArrayList<>();
    private final static Logger LOGGER = LoggerFactory.getLogger(ResourcePacksProvider.class);

    private final ResourceLocation location;
    private final ModInfo ownerMod;
    private boolean autoCompatibilityCompliance = true,
            shouldAddAutomatically = true,
            hasFixedPosition = false,
            isRequired = false;
    private @NotNull String packsDirPath = "resourcepacks";
    private @Nullable Component displayName = null;

    private ResourcePacksProvider(@NotNull ResourceLocation location, @NotNull ModInfo ownerMod) {
        this.ownerMod = ownerMod;
        this.location = location;
    }

    public static @NotNull ResourcePacksProvider of(@NotNull ResourceLocation location) {
        return new ResourcePacksProvider(location, ModInfo.getOrThrow(location.getNamespace()));
    }

    public @NotNull ResourcePacksProvider setAutoCompatibilityCompliance(boolean autoCompatibilityCompliance) {
        this.autoCompatibilityCompliance = autoCompatibilityCompliance;
        return this;
    }

    public @NotNull ResourcePacksProvider setShouldAddAutomatically(boolean shouldAddAutomatically) {
        this.shouldAddAutomatically = shouldAddAutomatically;
        return this;
    }

    public void setHasFixedPosition(boolean hasFixedPosition) {
        this.hasFixedPosition = hasFixedPosition;
    }

    public void setRequired(boolean required) {
        isRequired = required;
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
        final var optionalResourcePacksPath = ownerMod.findPath(packsDirPath);

        if (optionalResourcePacksPath.isEmpty()) {
            LOGGER.error(
                    "Resourcepack `{}` could not be registered because resourcepacks directory `{}` does not exist",
                    location,
                    ownerMod.getRootPaths().get(0) + ownerMod.getFileSystem().getSeparator() + packsDirPath
            );
            return false;
        }

        final var resourcePackPath = optionalResourcePacksPath.get()
                .resolve(location.getPath())
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(resourcePackPath)) {
            LOGGER.error(
                    "Resourcepack `{}` could not be registered because resourcepack directory `{}` does not exist",
                    location,
                    resourcePackPath
            );
            return false;
        }

        final var finishedDisplayName = displayName != null ? displayName : Component.translatable(
                "%s.resourcepack.%s.title".formatted(
                        location.getNamespace(),
                        location.getPath().replaceAll("[/\\\\]+", ".")
                )
        );

        return createPack(
                Platform.getEnvironment().isClient() ? PackType.CLIENT_RESOURCES : PackType.SERVER_DATA,
                "pack$" + location,
                resourcePackPath,
                finishedDisplayName
        ).map(pack -> {
            (Platform.getEnvironment().isClient() ? CLIENT_RESOURCE_PACKS : SERVER_RESOURCE_PACKS).put(pack.getId(), pack);
            return true;
        }).orElse(false);
    }

    private @NotNull PackLocationInfo createPackLocationInfo(
            @NotNull String packId,
            @NotNull Component finishedDisplayName
    ) {
        final var knownPack = new KnownPack(
                ownerMod.getId(),
                packId,
                ownerMod.getVersion()
        );
        final var packSource = new PackSource() {
            @Override
            public @NotNull Component decorate(@NotNull Component packName) {
                return packName;
            }

            @Override
            public boolean shouldAddAutomatically() {
                return shouldAddAutomatically;
            }
        };

        return new PackLocationInfo(
                packId,
                finishedDisplayName,
                packSource,
                Optional.of(knownPack)
        );
    }

    private @NotNull Optional<Pack> createPack(
            @NotNull PackType packType,
            @NotNull String packId,
            @NotNull Path resourcePackPath,
            @NotNull Component finishedDisplayName
    ) {
        final var packLocationInfo = createPackLocationInfo(packId, finishedDisplayName);
        final var resourceSupplier = createResourceSupplier(resourcePackPath);

        var defaultMetadata = Optional.ofNullable(Pack.readPackMetadata(
                packLocationInfo,
                resourceSupplier,
                SharedConstants.getCurrentVersion().getPackVersion(packType)
        ));

        if (autoCompatibilityCompliance)
            defaultMetadata = defaultMetadata.map(metadata -> new Pack.Metadata(
                    metadata.description(),
                    PackCompatibility.COMPATIBLE,
                    metadata.requestedFeatures(),
                    metadata.overlays()
            ));

        return defaultMetadata.map(metadata -> new Pack(
                packLocationInfo,
                resourceSupplier,
                metadata,
                new PackSelectionConfig(
                        hasFixedPosition,
                        Pack.Position.TOP,
                        isRequired
                )
        ));
    }

    private static @NotNull Pack.ResourcesSupplier createResourceSupplier(
            @NotNull Path resourcePackPath
    ) {
        return new Pack.ResourcesSupplier() {
            @Override
            public @NotNull PackResources openPrimary(@NotNull PackLocationInfo packLocationInfo) {
                return new PathPackResources(packLocationInfo, resourcePackPath);
            }

            @Override
            public @NotNull PackResources openFull(
                    @NotNull PackLocationInfo packLocationInfo,
                    @NotNull Pack.Metadata metadata
            ) {
                return new PathPackResources.PathResourcesSupplier(resourcePackPath).openFull(
                        packLocationInfo,
                        metadata
                );
            }
        };
    }

    public static void consumeBuiltinPacks(@NotNull PackType type, Consumer<Pack> packConsumer) {
        final var builtinPacks = type == PackType.CLIENT_RESOURCES ? CLIENT_RESOURCE_PACKS : SERVER_RESOURCE_PACKS;

        builtinPacks.values().forEach(packConsumer);
    }

    public static @NotNull List<String> getDefaultEnabledResourcePackNames() {
        return DEFAULT_ENABLED_RESOURCE_PACK_NAMES;
    }
}
