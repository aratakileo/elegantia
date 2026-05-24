package io.github.aratakileo.elegantia.core.util;

import io.github.aratakileo.elegantia.core.Result;
import io.github.aratakileo.elegantia.common.environment.Loader;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.LazySafeInitializer;
import io.github.aratakileo.elegantia.core.reflection.ClassContainer;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Arrays;

@ApiStatus.Experimental
public final class Classes {
    private final static LazySafeInitializer<Object> MAPPING_RESOLVER;
    private final static LazySafeInitializer<RuntimeMapping> RUNTIME_MAPPING;

    private Classes() {}

    @SuppressWarnings("unchecked")
    public static <T, R> @NotNull R quietCast(@NotNull T value) {
        return (R) value;
    }

    public static boolean exists(@NotNull String clazzName) {
        return load(clazzName).isOkay();
    }

    public static boolean anyExists(@NotNull String @NotNull... clazzNames) {
        return Arrays.stream(clazzNames).anyMatch(Classes::exists);
    }

    public static boolean allExists(@NotNull String @NotNull... clazzNames) {
        return Arrays.stream(clazzNames).allMatch(Classes::exists);
    }

    public static @NotNull Result<Class<?>> load(@NotNull String className) {
        return Result.fromFactory(() -> load(context(), obfuscated(className)));
    }

    public static @NotNull Result<Class<?>> load(@NotNull Origin origin, @NotNull String className) {
        return load(origin.logger(), className);
    }

    public static @NotNull Result<Class<?>> load(@NotNull Logger logger, @NotNull String className) {
        return load(className).runIfOkay(clazz -> logger.info(
                "The class `{}` has been successfully loaded",
                clazz.getName()
        ));
    }

    public static @NotNull String sourcesLoadPath(@NotNull Class<?> clazz) {
        return clazz.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static @NotNull RuntimeMapping runtimeMapping() {
        if (!Loader.current().fabricBased() || !MAPPING_RESOLVER.initialized())
            return RuntimeMapping.MOJANG;

        return RUNTIME_MAPPING.unwrap();
    }

    public static @NotNull String obfuscated(final @NotNull String name) {
        // `Loader.currentDefined()` is really necessary cuz of `loadClass(name)` inside `Loader.current()`,
        // and `obfuscated(name)` is called inside this `loadClass(name)`,
        // so without `Loader.currentDefined()` it'll lead to infinity recursion

        if (!Loader.currentDefined() || !Loader.current().fabricBased()) return name;
        if (runtimeMapping() == RuntimeMapping.MOJANG) return name;
        if (!name.startsWith("net.minecraft.") || !MAPPING_RESOLVER.initialized()) return name;

        // DO NOT USE REFLECTION CONTAINERS HERE FOR PERFORMANCE REASONS
        return Result.fromFactory(
                () -> (String)MAPPING_RESOLVER.unwrap().getClass()
                        .getMethod("mapClassName", String.class, String.class)
                        .invoke(
                                MAPPING_RESOLVER.unwrap(),
                                "official",
                                name.equals("net.minecraft.client.Minecraft")
                                        ? "net.minecraft.client.MinecraftClient"
                                        : name
                        )
        ).throwOrLogIfError(Origin.ELEGANTIA).orElse(name);
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

    static {
        MAPPING_RESOLVER = LazySafeInitializer.create(() -> ClassContainer.load("net.fabricmc.loader.api.FabricLoader")
                .method("getInstance")
                .containedStaticCall()
                .methodCaller("getMappingResolver")
                .call()
                .throwOrLogIfError(Origin.ELEGANTIA)
                .orElse(null) // ignore warnings
        );

        RUNTIME_MAPPING = LazySafeInitializer.create(() -> {
            final Object resolver = MAPPING_RESOLVER.unwrap();

            return ClassContainer.create(resolver.getClass())
                    .method("getCurrentRuntimeNamespace")
                    .instanceCall(resolver)
                    .map(
                            mappingName -> mappingName.equals("official")
                                    ? RuntimeMapping.MOJANG
                                    : RuntimeMapping.OBFUSCATED
                    ).throwOrLogIfError(Origin.ELEGANTIA)
                    .orElse(RuntimeMapping.MOJANG);
        });
    }
}
