package io.github.aratakileo.elegantia.util;

import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;

public interface Random {
    static int getInt(int min, int max) {
        return RandomSource.create().nextIntBetweenInclusive(min, max);
    }

    static <T> T select(@NotNull T[] array) {
        return array[RandomSource.create().nextInt(array.length)];
    }
}
