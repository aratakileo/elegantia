package io.github.aratakileo.elegantia.mixin;

import com.google.gson.Gson;
import io.github.aratakileo.elegantia.ElegantiaConfig;
import io.github.aratakileo.elegantia.util.ResourcePacksProvider;
import net.minecraft.client.Options;
import net.minecraft.server.packs.repository.PackRepository;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Mixin(Options.class)
public class OptionsMixin {
    @Shadow
    public List<String> resourcePacks;

    @Shadow
    private static List<String> readPackList(String content) {
        throw new IllegalStateException("Injection failed.");
    }

    @Shadow
    @Final
    static Gson GSON;

    @Unique
    private List<String> elegantia$defaultEnabledResourcePacks = new ArrayList<>();

    @Inject(method = "processOptions", at = @At("HEAD"))
    private void processOption(Options.FieldAccess fieldAccess, CallbackInfo ci) {
        elegantia$defaultEnabledResourcePacks = fieldAccess.process(
                "elegantia$defaultEnabledResourcePacks",
                elegantia$defaultEnabledResourcePacks,
                OptionsMixin::readPackList,
                GSON::toJson
        );
    }

    @Inject(method = "loadSelectedResourcePacks", at = @At("HEAD"))
    private void loadSelectedResourcePacks(PackRepository repository, CallbackInfo ci) {

        elegantia$defaultEnabledResourcePacks.removeIf(name -> Objects.isNull(repository.getPack(name)));

        for (final var name: ResourcePacksProvider.getDefaultEnabledResourcePackNames()) {
            if (elegantia$defaultEnabledResourcePacks.contains(name)) continue;

            resourcePacks.add(name);
            elegantia$defaultEnabledResourcePacks.add(name);
        }
    }
}
