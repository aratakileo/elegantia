package io.github.aratakileo.elegantia.fabric;

import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.fabric.profile.FabricProfileProviderImpl;
import io.github.aratakileo.elegantia.client.event.CreativeTabModifier;
import io.github.aratakileo.elegantia.common.event.LootTableModifier;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class FabricInitialisation {
    private FabricInitialisation() {}

    public static void init() {
        Loader.setProfileProvider(FabricProfileProviderImpl.INSTANCE);

        FabricRegistryServiceImpl.impl();

        CreativeTabModifier.SUBSCRIBERS.register(
                ((tab, event) -> ItemGroupEvents.modifyEntriesEvent(tab)
                        .register(output -> event.invoker().onModify(output::accept)))
        );

        LootTableEvents.MODIFY.register((
                key,
                tableBuilder,
                source,
                registries
        ) -> LootTableModifier.MODIFY.invoker().onModify(
                key,
                tableBuilder::pool,
                switch (source) {
                    case VANILLA -> LootTableModifier.Source.VANILLA;
                    case MOD -> LootTableModifier.Source.MOD;
                    case DATA_PACK, REPLACED -> LootTableModifier.Source.DATAPACK;
                }
        ));
    }
}
