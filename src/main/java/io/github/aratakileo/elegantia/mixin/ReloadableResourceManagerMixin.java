package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.event.ResourceReloadListener;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Mixin(ReloadableResourceManager.class)
public class ReloadableResourceManagerMixin {
    @Final
    @Shadow
    private PackType type;

    @Final
    @Shadow
    private List<PreparableReloadListener> listeners;

    @Inject(method = "createReload", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;isDebugEnabled()Z", remap = false))
    private void createReload(Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture, List<PackResources> list, CallbackInfoReturnable<ReloadInstance> cir) {
        listeners.removeAll(ResourceReloadListener.getListeners());

        if (type == PackType.CLIENT_RESOURCES) listeners.addAll(ResourceReloadListener.getListeners());
    }
}
