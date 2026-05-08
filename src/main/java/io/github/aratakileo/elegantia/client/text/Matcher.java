package io.github.aratakileo.elegantia.client.text;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public abstract class Matcher {
    public final @NotNull ResourceLocation id;

    public Matcher(@NotNull ResourceLocation id) {
        this.id = id;
    }

    abstract void match(@NotNull MatchContext context);
}
