package io.github.aratakileo.elegantia.gui.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.aratakileo.elegantia.Elegantia;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Config {
    private final static Gson gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private final static ConcurrentHashMap<Class<? extends Config>, ConfigInfo> configs = new ConcurrentHashMap<>();

    public void setFieldValue(@NotNull String fieldName, @Nullable Object value) {
        try {
            getClass().getField(fieldName).set(this, value);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Elegantia.LOGGER.warn("Failed to set config field `" + fieldName + '`', e);
        }
    }

    public @Nullable Object getFieldValue(@NotNull String fieldName) {
        try {
            return getClass().getField(fieldName).get(this);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            Elegantia.LOGGER.warn("Failed to get config field `" + fieldName + '`', e);
            return null;
        }
    }

    public boolean getBooleanFieldValue(@NotNull String fieldName) {
        return (Boolean) getFieldValue(fieldName);
    }

    public static <T extends Config> @Nullable T load(@NotNull Class<T> configClass, @NotNull File file) {
        if (file.exists()) {
            try {
                final var fileReader = new FileReader(file);
                final var configInstance = gson.fromJson(fileReader, configClass);

                fileReader.close();

                return configInstance;
            } catch (Exception e) {
                Elegantia.LOGGER.error("Failed to load config by path `" + file.getPath() + "`: ", e);
            }

            return null;
        }

        try {
            final var configInstance = configClass.getConstructor().newInstance();

            save(configInstance, file);

            return configInstance;
        } catch (Exception e) {
            Elegantia.LOGGER.error("Failed to create a new instance of `" + configClass.getName() + "`: ", e);
        }

        return null;
    }

    public static void save(@NotNull Config configInstance, @NotNull File file) {
        final var parentFile = file.getParentFile();

        if (!parentFile.exists())
            parentFile.mkdir();

        try {
            final var fileWriter = new FileWriter(file);
            fileWriter.write(gson.toJson(configInstance));
            fileWriter.close();
        } catch (Exception e) {
            Elegantia.LOGGER.error("Failed to save config by path `" + file.getPath() + "`: ", e);
        }
    }

    public static <T extends Config> @Nullable T init(@NotNull Class<T> configClass, @NotNull String modId) {
        if (Modifier.isAbstract(configClass.getModifiers()))
            throw new ConfigInitException(
                    "Abstract classes such as `" + configClass.getName() + "` is not allowed here!"
            );


        if (configs.containsKey(configClass)) {
            Elegantia.LOGGER.warn("Config `" + configClass.getName() + "` is already inited!");
            return null;
        }

        if (FabricLoader.getInstance().getModContainer(modId).isEmpty())
            throw new ConfigInitException("Mod with id `" + modId + "` does not exist!");

        final var fields = new ArrayList<Field>();

        for (final var field: configClass.getFields()) {
            if (!field.isAnnotationPresent(ConfigField.class)) continue;

            if (field.getType() != boolean.class) {
                Elegantia.LOGGER.warn(
                        "Field `"
                                + field.getName()
                                + "` of `"
                                + configClass.getName()
                                + "` has unsupported config field type. This field will be skipped"
                );
                continue;
            }

            fields.add(field);
        }

        final var configInstance = load(configClass, new File("config/" + modId + ".json"));

        if (Objects.isNull(configInstance)) {
            Elegantia.LOGGER.warn("Failed to init config `" + configClass.getName() + '`');
            return null;
        }

        configs.put(configClass, new ConfigInfo(modId, configInstance, fields));

        return configInstance;
    }

    public static @Nullable ConfigInfo getConfigInfo(@NotNull Class<? extends Config> configClass) {
        return configs.get(configClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Config> @Nullable T getInstance(@NotNull Class<T> configClass) {
        if (!configs.containsKey(configClass))
            return null;

        return (T) configs.get(configClass).configInstance;
    }

    public record ConfigInfo(@NotNull String modId, @NotNull Config configInstance, @NotNull List<Field> fields) {}

    public static class ConfigInitException extends RuntimeException {
        public ConfigInitException(@NotNull String message) {
            super(message);
        }
    }
}
