package io.github.aratakileo.elegantia.forge;

import io.github.aratakileo.elegantia.client.event.CreativeTabModifier;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.listener.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@Mod.EventBusSubscriber(modid = ForgeInitialisation.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class ForgeCreativeTabModifier {
    private ForgeCreativeTabModifier() {}

    @SubscribeEvent
    public static void modify(@NotNull BuildCreativeModeTabContentsEvent forgeEvent) {
        CreativeTabModifier.invoke((tab, event) -> {
            if (forgeEvent.getTabKey() == tab) event.invoker().onModify(forgeEvent::accept);
        });
    }
}
