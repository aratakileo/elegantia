package io.github.aratakileo.elegantia.framework.math;

import io.github.aratakileo.elegantia.util.Strings;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import org.jetbrains.annotations.NotNull;

public final class Probability {
    public static final Probability NEVER = new Probability(0.0f);
    public static final Probability UNLIKELY = new Probability(0.3f);
    public static final Probability EVEN = new Probability(0.5f);
    public static final Probability LIKELY = new Probability(0.7f);
    public static final Probability ALWAYS = new Probability(1.0f);

    private final float value;

    private Probability(float value) {
        if (value < 0.0f || value > 1.0f)
            throw new IllegalArgumentException(Strings.format(
                    "probability value must be between 0.0 and 1.0 (got {})",
                    value
            ));

        this.value = value;
    }

    public float decimal() {
        return value;
    }

    public float percentage() {
        return value * 100f;
    }

    public @NotNull LootItemCondition loot() {
        return LootItemRandomChanceCondition.randomChance(value).build();
    }

    public @NotNull LootItemCondition.Builder lootBuilder() {
        return LootItemRandomChanceCondition.randomChance(value);
    }

    public boolean roll(@NotNull RandomSource source) {
        return source.nextFloat() < value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Probability that)) return false;
        return Float.compare(that.value, value) == 0;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(value);
    }

    @Override
    public String toString() {
        return percentage() + "%";
    }

    public static @NotNull Probability decimal(float value) {
        if (Float.compare(value, 0.0f) == 0) return NEVER;
        if (Float.compare(value, 0.3f) == 0) return UNLIKELY;
        if (Float.compare(value, 0.5f) == 0) return EVEN;
        if (Float.compare(value, 0.7f) == 0) return LIKELY;
        if (Float.compare(value, 1.0f) == 0) return ALWAYS;

        return new Probability(value);
    }

    public static @NotNull Probability percentage(float value) {
        if (Float.compare(value, 0.0f) == 0) return NEVER;
        if (Float.compare(value, 30.0f) == 0) return UNLIKELY;
        if (Float.compare(value, 50.0f) == 0) return EVEN;
        if (Float.compare(value, 70.0f) == 0) return LIKELY;
        if (Float.compare(value, 100.0f) == 0) return ALWAYS;

        return new Probability(value / 100f);
    }
}
