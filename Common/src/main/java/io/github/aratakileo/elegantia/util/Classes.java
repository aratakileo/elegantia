package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.core.environment.Loader;
import io.github.aratakileo.elegantia.core.environment.Origin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;

public final class Classes {
    private static Object fabricLoader = null, mappingResolver = null;

    private Classes() {}

    @SuppressWarnings("unchecked")
    public static <T, R> @NotNull R quietCast(@NotNull T value) {
        return (R) value;
    }

    public static boolean exists(@NotNull String className) {
        return load(className).isOkay();
    }

    public static boolean anyExists(@NotNull String @NotNull... classNames) {
        return Arrays.stream(classNames).anyMatch(Classes::exists);
    }

    public static boolean allExists(@NotNull String @NotNull... classNames) {
        return Arrays.stream(classNames).allMatch(Classes::exists);
    }

    public static @NotNull Result<Class<?>> load(@NotNull String className) {
        return Result.fromFactory(() -> load(context(), obfuscated(className)));
    }

    public static @NotNull Result<Class<?>> load(@NotNull Origin origin, @NotNull String className) {
        return load(origin.logger(), className);
    }

    public static @NotNull Result<Class<?>> load(@NotNull Logger logger, @NotNull String className) {
        return load(className).runIfOkay(clazz -> logger.info(
                "Class `{}` has been successfully loaded",
                clazz.getName()
        ));
    }

    public static @NotNull String sourcesLoadPath(@NotNull Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static @NotNull RuntimeMapping runtimeMapping() {
        if (!Loader.current().fabricBased() || !checkMappingResolverInitialisation())
            return RuntimeMapping.MOJANG;

        return Result.fromFactory(
                () -> (String)mappingResolver.getClass()
                        .getMethod("getCurrentRuntimeNamespace")
                        .invoke(mappingResolver)
        ).map(mappingName -> mappingName.equals("official") ? RuntimeMapping.MOJANG : RuntimeMapping.OBFUSCATED)
                .throwOrLogIfError(Origin.ELEGANTIA)
                .orElse(RuntimeMapping.MOJANG);
    }

    public static @NotNull String obfuscated(final @NotNull String name) {
        // `Loader.currentDefined()` is really necessary cuz of `load(name)` inside `Loader.current()`,
        // and `obfuscated(name)` is called inside this `load(name)`,
        // so without `Loader.currentDefined()` it'll lead to infinity recursion

        if (!Loader.currentDefined() || !Loader.current().fabricBased()) return name;
        if (runtimeMapping() == RuntimeMapping.MOJANG) return name;
        if (!name.startsWith("net.minecraft.") || !checkMappingResolverInitialisation()) return name;

        return Result.fromFactory(
                () -> (String)mappingResolver.getClass()
                        .getMethod("mapClassName", String.class, String.class)
                        .invoke(
                                mappingResolver,
                                "official",
                                name.equals("net.minecraft.client.Minecraft")
                                        ? "net.minecraft.client.MinecraftClient"
                                        : name
                        )
        ).throwOrLogIfError(Origin.ELEGANTIA).orElse(name);
    }

    private static boolean checkMappingResolverInitialisation() {
        if (fabricLoader == null) try {
            final var context = context();

            fabricLoader = load(context, "net.fabricmc.loader.api.FabricLoader")
                    .getMethod("getInstance")
                    .invoke(null);

            mappingResolver = fabricLoader.getClass()
                    .getMethod("getMappingResolver")
                    .invoke(fabricLoader);
        } catch (Exception e) {
            Exceptions.throwOrLog(Origin.ELEGANTIA, () -> new RuntimeException(e));
            return false;
        }

        return true;
    }

    private static @NotNull Class<?> load(
            @Nullable ClassLoader context,
            @NotNull String className
    ) throws ClassNotFoundException {
        return Class.forName(className, true, context != null ? context : Classes.class.getClassLoader());
    }

    private static @NotNull ClassLoader context() {
        return Thread.currentThread().getContextClassLoader();
    }

    public enum RuntimeMapping {
        MOJANG,
        OBFUSCATED;
    }
}
