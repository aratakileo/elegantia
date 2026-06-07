package io.github.aratakileo.elegantia.common.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.ToNumberPolicy;
import io.github.aratakileo.elegantia.common.environment.Origin;
import io.github.aratakileo.elegantia.core.util.Exceptions;
import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;import java.util.concurrent.ConcurrentHashMap;

public final class ConfigManager {
    private final static ConcurrentHashMap<String, ConcurrentHashMap<String, ConfigValue<?, ?>>> DATA;
    private final static Type MAP_TYPE = new TypeToken<Map<String, Object>>(){}.getType();

    public final static Gson JSON_CODEC;

    private ConfigManager() {}

    public static @NotNull Optional<ConfigValue<?, ?>> optionalValue(@NotNull Origin origin, @NotNull String key) {
        return Optional.ofNullable(DATA.get(origin.key))
                .map(config -> config.get(key));
    }

    public static @NotNull Map<String, Object> unwrapJsonable(@NotNull Origin origin) {
        final var jsonValues = new HashMap<String, Object>();

        for (final var value: DATA.get(origin.key).values())
            jsonValues.put(value.unwrapName(), value.unwrapJsonable());

        return jsonValues;
    }

    public static void load(@NotNull Class<?> container, @NotNull Origin origin) {
        load(container, origin, file(origin));
    }

    public static void load(@NotNull Class<?> container, @NotNull Origin origin, @NotNull File configFile) {
        DATA.computeIfAbsent(origin.key, id -> {
            final var configValues = new ConcurrentHashMap<String, ConfigValue<?, ?>>();
            final var jsonValues = read(origin, configFile);

            for (final var field: container.getFields()) {
                final var modifiers = field.getModifiers();

                if (!Modifier.isStatic(modifiers) || !Modifier.isPublic(modifiers)) continue;
                if (!ConfigValue.class.isAssignableFrom(field.getType())) continue;

                try {
                    final var value = (ConfigValue<?, ?>)field.get(null);

                    if (!value.hasName()) value.name(Strings.camelToSnake(field.getName()));

                    final var loadedValuePassedFilters = jsonValues.containsKey(value.unwrapName())
                            ? value.load(jsonValues.get(value.unwrapName()))
                            : value.load(value.unwrapDefault());

                    if (!loadedValuePassedFilters) {
                        if (value.load(value.unwrapDefault())) throw new IllegalStateException();

                        origin.logger().error(
                                "a value `{}` of the key `{}` have not passed the filters while loading",
                                jsonValues.get(value.unwrapName()),
                                value.unwrapName()
                        );
                    }

                    configValues.computeIfAbsent(value.unwrapName(), name -> value);
                } catch (Throwable e) {
                    origin.logger().error("failed to load a config value for a field `{}`", field.getName(), e);
                }
            }

            return configValues;
        });
    }

    public static void save(@NotNull Origin origin) {
        save(origin, file(origin));
    }

    public static void save(@NotNull Origin origin, @NotNull File configFile) {
        if (Exceptions.throwOrLogIf(
                !DATA.containsKey(origin.key),
                origin,
                IllegalStateException::new,
                "can not save a configuration file before it has been ever loaded"
        )) return;

        final var dir = configFile.getParentFile();

        if (Exceptions.throwOrLogIf(
                !dir.exists() && !dir.mkdir(),
                origin,
                IOException::new,
                "failed to make configuration directory `{}`",
                dir.getPath()
        )) return;

        try (final var fileWriter = new FileWriter(configFile)) {
            fileWriter.write(JSON_CODEC.toJson(unwrapJsonable(origin), MAP_TYPE));
            for (final var value: DATA.get(origin.key).values()) value.markAsNonDirty();
        } catch (Throwable e) {
            Exceptions.throwOrLog(origin, () -> e);
        }
    }

    public static @NotNull File file(@NotNull Origin origin) {
        return new File("config/" + origin.key + ".json");
    }

    private static @NotNull Map<String, Object> read(@NotNull Origin origin, @NotNull File file) {
        if (!file.exists()) return Map.of();

        try (final var fileReader = new FileReader(file)) {
            return JSON_CODEC.fromJson(fileReader, MAP_TYPE);
        } catch (Throwable e) {
            Exceptions.throwOrLog(origin, () -> e);
        }

        return Map.of();
    }

    static {
        DATA = new ConcurrentHashMap<>();

        JSON_CODEC = new GsonBuilder()
                .setFieldNamingStrategy(field -> Strings.camelToSnake(field.getName()))
                .setObjectToNumberStrategy(ToNumberPolicy.LONG_OR_DOUBLE)
                .setPrettyPrinting()
                .create();
    }
}
