package io.github.aratakileo.elegantia.event;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Works only with client-side resources
 */
public final class ResourceReloadListener {
    private static final ArrayList<PreparableReloadListener> listeners = new ArrayList<>();
    private ResourceReloadListener() {}

    public static @NotNull List<PreparableReloadListener> getListeners() {
        return listeners;
    }

    public static void register(@NotNull ResourceManagerReloadListener listener) {
        listeners.add(listener);
    }
}
