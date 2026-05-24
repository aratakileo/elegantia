package io.github.aratakileo.elegantia.client.event;

import io.github.aratakileo.elegantia.core.Event;
import io.github.aratakileo.elegantia.common.resource.RegistryContainer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ConcurrentHashMap;

public final class CreativeTabModifier {
    private final static ConcurrentHashMap<ResourceKey<CreativeModeTab>, Event<Listener>> EVENTS = new ConcurrentHashMap<>();

    public final static Event<InvokeListener> SUBSCRIBERS;

    private CreativeTabModifier() {}

    private static @NotNull Event<Listener> createEvent() {
        return Event.create(
                Listener.class,
                callbacks -> output -> {
                    for (final var callback: callbacks) callback.onModify(output);
                }
        );
    }

    public static @NotNull Event<Listener> modify(@NotNull ResourceKey<CreativeModeTab> tab) {
        final var event = EVENTS.computeIfAbsent(tab, k -> createEvent());
        SUBSCRIBERS.invoker().invoke(tab, event);
        return event;
    }

    @SafeVarargs
    public static void modify(
            @NotNull ResourceKey<CreativeModeTab> tab,
            @NotNull RegistryContainer<Item> item,
            @NotNull RegistryContainer<Item> @NotNull... items
    ) {
        modify(tab).register(output -> {
            output.accept(item);

            for (final var _item: items)
                output.accept(_item);
        });
    }

    public static void invoke(@NotNull CreativeTabModifier.InvokeListener invoker) {
        for (final var eventEntry: EVENTS.entrySet())
            invoker.invoke(eventEntry.getKey(), eventEntry.getValue());
    }

    @FunctionalInterface
    public interface Listener {
        void onModify(@NotNull TabConsumer output);
    }

    @FunctionalInterface
    public interface InvokeListener {
        void invoke(@NotNull ResourceKey<CreativeModeTab> tab, @NotNull Event<Listener> event);
    }

    @FunctionalInterface
    public interface TabConsumer extends CreativeModeTab.Output {
        default void accept(@NotNull RegistryContainer<Item> item) {
            accept(item, CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        default void accept(@NotNull RegistryContainer<Item> item, @NotNull CreativeModeTab.TabVisibility visibility) {
            accept(item.optional().orElseThrow(
                    () -> new IllegalStateException("try to get access to non initialized item")
            ), visibility);
        }
    }

    static {
        SUBSCRIBERS = Event.create(
                InvokeListener.class,
                listeners -> (tab, event) -> {
                    for (final var listener: listeners) listener.invoke(tab, event);
                }
        );
    }
}
