package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.server.ResourcePacksProvider;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.BuiltInPackSource;
import net.minecraft.server.packs.repository.Pack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(BuiltInPackSource.class)
public class BuiltinPackSourceMixin {
    @Inject(method = "listBundledPacks", at = @At("RETURN"))
    private void listBundledPacks(Consumer<Pack> consumer, CallbackInfo ci) {
        if (((Object)this) instanceof ClientPackSource)
            ResourcePacksProvider.consumeBuiltinPacks(PackType.CLIENT_RESOURCES, consumer);
    }
}
