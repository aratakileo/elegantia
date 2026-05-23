package io.github.aratakileo.elegantia.core.mixin;

import io.github.aratakileo.elegantia.framework.data.AttachmentHolder;
import io.github.aratakileo.elegantia.framework.data.AttachmentKey;
import io.github.aratakileo.elegantia.framework.data.impl.AttachmentGroupImpl;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(Entity.class)
public abstract class EntityMixin implements AttachmentHolder<EntityMixin> {
    @Unique
    private final AttachmentGroupImpl elegantia$attachmentGroup = AttachmentGroupImpl.eleganted();

    @Override
    public @NotNull <T> Optional<T> optional(@NotNull AttachmentKey<T> key) {
        return elegantia$attachmentGroup.optional(key);
    }

    @Override
    public @NonNull <T> T unwrap(@NotNull AttachmentKey<T> key) {
        return elegantia$attachmentGroup.unwrap(key);
    }

    @Override
    public <T> AttachmentHolder<EntityMixin> set(@NotNull AttachmentKey<T> key, @Nullable T value) {
        elegantia$attachmentGroup.set(key, value);
        return this;
    }

    @Override
    public <T> boolean has(@NotNull AttachmentKey<T> key) {
        return elegantia$attachmentGroup.has(key);
    }

    @Inject(method = "saveWithoutId", at = @At("TAIL"))
    public void elegantia$saveWithoutId(@NotNull ValueOutput output, @NotNull CallbackInfo ci) {
        elegantia$attachmentGroup.write(output);
    }

    @Inject(method = "load", at = @At("TAIL"))
    public void elegantia$load(@NotNull ValueInput input, @NotNull CallbackInfo ci) {
        elegantia$attachmentGroup.read(input);
    }
}
