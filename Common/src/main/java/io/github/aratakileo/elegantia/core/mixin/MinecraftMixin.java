package io.github.aratakileo.elegantia.core.mixin;

import io.github.aratakileo.elegantia.core.DeltaTimeSupplier;
import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.framework.event.GuiTicker;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin implements DeltaTimeSupplier {
    @Unique
    private static long elegantia$lastSystemTime = -1;

    @Unique
    @Override
    public long deltaTime() {
        final var current = Util.getMillis();

        if (elegantia$lastSystemTime == -1)
            elegantia$lastSystemTime = current;

        return current - elegantia$lastSystemTime;
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void elegantia$runTick(boolean renderLevel, @NotNull CallbackInfo ci) {
//        Origin.ELEGANTIA.logger().info("Ticking {}ms", deltaTime());
        GuiTicker.TICK.invoker().onTick(deltaTime());
        elegantia$lastSystemTime = Util.getMillis();
    }
}
