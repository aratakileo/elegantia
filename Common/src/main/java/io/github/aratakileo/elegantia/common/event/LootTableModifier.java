package io.github.aratakileo.elegantia.common.event;

import io.github.aratakileo.elegantia.core.Event;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public final class LootTableModifier {
    public final static Event<Listener> MODIFY;

    private LootTableModifier() {}

    @FunctionalInterface
    public interface Listener {
        void onModify(
                @NotNull ResourceKey<LootTable> key,
                @NotNull Consumer<LootPool> poolConsumer,
                @NotNull Source source
        );
    }

    static {
        MODIFY = Event.create(
                Listener.class,
                callbacks -> (
                        key,
                        consumer,
                        source
                ) -> { for (final var callback: callbacks) callback.onModify(key, consumer, source); }
        );
    }

    public enum Source {
        VANILLA(true),

        /**
         * A loot table loaded from mods' bundled resources.
         *
         * <p>This includes the additional builtin data packs registered by mods
         * with Fabric Resource Loader.
         */
        MOD(true),

        /**
         * A loot table loaded from an external data pack.
         */
        DATAPACK(false);

        private final boolean builtin;

        Source(boolean builtin) {
            this.builtin = builtin;
        }

        /**
         * Returns whether this loot table source is builtin
         * and bundled in the vanilla or mod resources.
         *
         * <p>{@link #VANILLA} and {@link #MOD} are builtin.
         *
         * @return {@code true} if builtin, {@code false} otherwise
         */
        public boolean isBuiltin() {
            return builtin;
        }
    }
}
