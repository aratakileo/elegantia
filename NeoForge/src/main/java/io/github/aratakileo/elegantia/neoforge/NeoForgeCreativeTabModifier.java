package io.github.aratakileo.elegantia.neoforge;

import io.github.aratakileo.elegantia.client.event.CreativeTabModifier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import org.jetbrains.annotations.NotNull;

@EventBusSubscriber(modid = NeoForgeInitialisation.MOD_ID)
public final class NeoForgeCreativeTabModifier {
    private NeoForgeCreativeTabModifier() {}

    @SubscribeEvent
    public static void modify(@NotNull BuildCreativeModeTabContentsEvent neoforgeEvent) {
        CreativeTabModifier.invoke((tab, event) -> {
            if (neoforgeEvent.getTabKey() == tab) {
                event.invoker().onModify(neoforgeEvent::accept);
            }
        });
    }
}
