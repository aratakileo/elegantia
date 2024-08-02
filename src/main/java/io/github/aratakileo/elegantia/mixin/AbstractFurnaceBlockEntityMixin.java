package io.github.aratakileo.elegantia.mixin;

import io.github.aratakileo.elegantia.world.FuelController;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AbstractFurnaceBlockEntity.class)
public class AbstractFurnaceBlockEntityMixin {
    @Inject(at = @At("RETURN"), method = "getFuel")
    private static void fuelTimeMapHook(CallbackInfoReturnable<Map<Item, Integer>> info) {
        FuelController.applyFuels(info.getReturnValue());
    }
}
