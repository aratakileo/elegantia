package io.github.aratakileo.elegantia.neoforge;

import io.github.aratakileo.elegantia.common.event.LootTableModifier;
import io.github.aratakileo.elegantia.core.util.Strings;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@EventBusSubscriber(modid = NeoForgeInitialisation.MOD_ID)
public final class NeoForgeLootTableModifier {
    private NeoForgeLootTableModifier() {}

    @SubscribeEvent
    public static void onLootTableLoad(@NotNull LootTableLoadEvent event) {
        final var table = event.getTable();

        final var source = source(table.getLootTableId());
        final var resourceKey = ResourceKey.create(Registries.LOOT_TABLE, table.getLootTableId());

        LootTableModifier.MODIFY.invoker().onModify(resourceKey, table::addPool, source);
    }

    private static @NotNull LootTableModifier.Source source(@NotNull Identifier id) {
        if (ModList.get().isLoaded(id.getNamespace())) return LootTableModifier.Source.MOD;

        final var server = ServerLifecycleHooks.getCurrentServer();

        if (server == null) return LootTableModifier.Source.VANILLA;

        final var resourceId = Identifier.fromNamespaceAndPath(
                id.getNamespace(),
                Strings.format("loot_table/{}.json", id.getPath())
        );

        final var resources = server.getResourceManager();
        final var definedResource = resources.getResource(resourceId);

        if (definedResource.isEmpty()) return LootTableModifier.Source.VANILLA;

        final var packId = definedResource.get().sourcePackId();

        if (packId.equals("vanilla")) return LootTableModifier.Source.VANILLA;
        if (packId.startsWith("mod:")) return LootTableModifier.Source.MOD;

        return LootTableModifier.Source.DATAPACK;
    }
}
