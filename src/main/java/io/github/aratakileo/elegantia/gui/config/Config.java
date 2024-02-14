package io.github.aratakileo.elegantia.gui.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.aratakileo.elegantia.Elegantia;
import io.github.aratakileo.elegantia.gui.screen.ConfigScreen;
import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.util.ModInfo;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.regex.Pattern;

public abstract class Config {
    private final static Gson GSON = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setPrettyPrinting()
            .create();

    private final static Pattern TRIGGER_NAME_PATTERN = Pattern.compile("^(?![0-9-]+)[A-Za-z0-9_-]+[^-]$");

    private final static ConcurrentHashMap<Class<? extends Config>, ConfigInfo> CONFIGS_INFO = new ConcurrentHashMap<>();

    public boolean getTriggerValue(@NotNull String trigger) {
        final var triggerFields = Objects.requireNonNull(getConfigInfo(getClass())).triggerFields;

        if (!triggerFields.containsKey(trigger))
            throw new ConfigTriggerException("Trigger `" + trigger + "` does not exist");

        return getBooleanFieldValue(triggerFields.get(trigger));
    }

    public void setFieldValue(@NotNull String fieldName, @Nullable Object value) {
        try {
            getClass().getField(fieldName).set(this, value);
        } catch (IllegalAccessException e) {
            Elegantia.LOGGER.warn(
                    "Failed to set config field `" + Classes.getFieldOrMethodView(getClass(), fieldName) + '`',
                    e
            );
        } catch (NoSuchFieldException e) {
            Elegantia.LOGGER.error(
                    "Failed to set a non-existent config field `"
                            + Classes.getFieldOrMethodView(getClass(), fieldName)
                            + '`',
                    e
            );
        }
    }

    public @Nullable Object getFieldValue(@NotNull String fieldName) {
        try {
            return getClass().getField(fieldName).get(this);
        } catch (IllegalAccessException e) {
            Elegantia.LOGGER.warn(
                    "Failed to get config field `" + Classes.getFieldOrMethodView(getClass(), fieldName) + '`',
                    e
            );
        } catch (NoSuchFieldException e) {
            Elegantia.LOGGER.error(
                    "Failed to get a non-existent config field `"
                            + Classes.getFieldOrMethodView(getClass(), fieldName)
                            + '`',
                    e
            );
        }

        return null;
    }

    public boolean getBooleanFieldValue(@NotNull String fieldName) {
        return (Boolean) getFieldValue(fieldName);
    }

    public void invertBooleanFieldValue(@NotNull String fieldName) {
        setFieldValue(fieldName, !getBooleanFieldValue(fieldName));
    }

    public int getIntegerFieldValue(@NotNull String fieldName) {
        return (Integer) getFieldValue(fieldName);
    }

    public float getFloatFieldValue(@NotNull String fieldName) {
        return (Float) getFieldValue(fieldName);
    }

    public double getDoubleFieldValue(@NotNull String fieldName) {
        return (Double) getFieldValue(fieldName);
    }

    public String getStringFieldValue(@NotNull String fieldName) {
        return (String) getFieldValue(fieldName);
    }

    public void save() {
        save(this, Objects.requireNonNull(getConfigFile(this.getClass())));
    }

    public @Nullable ConfigScreen getScreen() {
        return ConfigScreen.of(getClass());
    }

    public @Nullable ConfigScreen getScreen(@NotNull Screen parent) {
        return ConfigScreen.of(getClass(), parent);
    }

    public static <T extends Config> @Nullable T load(@NotNull Class<T> configClass, @NotNull File file) {
        if (file.exists()) {
            try {
                final var fileReader = new FileReader(file);
                final var configInstance = GSON.fromJson(fileReader, configClass);

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

        if (!parentFile.exists() && !parentFile.mkdir())
            Elegantia.LOGGER.warn(
                    "Failed to make dir `"
                            + parentFile.getPath()
                            + "`. Possible reason: insufficient permissions to perform this action"
            );

        try {
            final var fileWriter = new FileWriter(file);
            fileWriter.write(GSON.toJson(configInstance));
            fileWriter.close();
        } catch (Exception e) {
            Elegantia.LOGGER.error("Failed to save config by path `" + file.getPath() + "`: ", e);
        }
    }

    public static @Nullable File getConfigFile(@NotNull Class<? extends Config> configClass) {
        if (!CONFIGS_INFO.containsKey(configClass))
            return null;

        return getConfigFile(CONFIGS_INFO.get(configClass).modId);
    }

    /**
     * @return Theoretically possible config file for the specified mod id
     */
    public static @NotNull File getConfigFile(@NotNull String modId) {
        return new File("config/" + modId + ".json");
    }

    public static <T extends Config> @Nullable T init(@NotNull Class<T> configClass, @NotNull String modId) {
        if (Modifier.isAbstract(configClass.getModifiers()))
            throw new ElegantiaConfigException(
                    "Abstract classes such as `" + configClass.getName() + "` is not allowed here!"
            );

        if (CONFIGS_INFO.containsKey(configClass)) {
            Elegantia.LOGGER.warn("Config `" + configClass.getName() + "` is already inited!");
            return null;
        }

        if (!ModInfo.isModLoaded(modId))
            throw new ElegantiaConfigException("Mod with id `" + modId + "` does not exist!");

        final var configInstance = load(configClass, getConfigFile(modId));

        if (Objects.isNull(configInstance)) {
            Elegantia.LOGGER.warn("Failed to init config `" + configClass.getName() + '`');
            return null;
        }

        final var entryFields = new ArrayList<Field>();
        final var entries = new ArrayList<EntryInfo>();
        final var triggeredFields = new HashMap<String, String>();

        for (final var field: configClass.getFields()) {
            final var isTrigger = field.isAnnotationPresent(Trigger.class);

            if (!field.isAnnotationPresent(ConfigEntry.class)) {
                if (isTrigger)
                    Elegantia.LOGGER.warn(
                            "Field `"
                                    + Classes.getFieldView(field)
                                    + "` is not marked with the @ConfigField annotation "
                                    + "which means that this field will not be displayed "
                                    + "on the mod configuration screen"
                    );

                continue;
            }

            if (field.getType() != boolean.class) {
                Elegantia.LOGGER.warn(
                        "Field `"
                                + Classes.getFieldView(field)
                                + "` has unsupported config field type. This field will be skipped"
                );
                continue;
            }

            entryFields.add(field);

            if (isTrigger) {
                final var triggerName = field.getAnnotation(Trigger.class).value();

                if (!TRIGGER_NAME_PATTERN.matcher(triggerName).find())
                    throw new ConfigTriggerException(
                            "Invalid trigger name `"
                                    + triggerName
                                    + "` does not match the pattern `"
                                    + TRIGGER_NAME_PATTERN
                                    + "` (trigger field: `"
                                    + Classes.getFieldView(field)
                                    + "`)!"
                    );

                if (!field.getAnnotation(ConfigEntry.class).triggeredBy().isEmpty()) {
                    throw new ConfigTriggerException(
                            "Trigger cannot be triggered (trigger field: "
                                    + Classes.getFieldView(field)
                                    + ")!"
                    );
                }

                triggeredFields.put(triggerName, field.getName());
            }
        }

        for (final var field: entryFields) {
            final var entryAnnotation = field.getAnnotation(ConfigEntry.class);
            final var triggeredBy = entryAnnotation.triggeredBy();

            if (triggeredBy.isEmpty()) continue;

            if (!triggeredFields.containsKey(triggeredBy))
                throw new ElegantiaConfigException(
                        "Field `"
                                + Classes.getFieldView(field)
                                + "` indicates a dependency on trigger `"
                                + triggeredBy
                                + "`, which does not exist!"
                );


            entries.add(EntryInfo.value(
                    EntryInfo.Type.BOOLEAN,
                    field.getName(),
                    field.isAnnotationPresent(Trigger.class) ? field.getAnnotation(Trigger.class).value() : null,
                    triggeredBy,
                    entryAnnotation.translationKey()
            ));
        }

        for (final var method: configClass.getMethods()) {
            if (!method.isAnnotationPresent(ConfigEntry.class)) continue;

            if (method.getReturnType() != void.class)
                throw new ElegantiaConfigException(
                        "Method `"
                                + Classes.getMethodView(method)
                                + "` cannot be represented as an action because it has a return type other than `void`"
                );

            if (method.getParameterCount() != 0)
                throw new ElegantiaConfigException(
                        "Method `"
                                + Classes.getMethodView(method)
                                + "` cannot be represented as an action because it has parameters"
                );

            final var annotationEntry = method.getAnnotation(ConfigEntry.class);
            final var triggeredBy = annotationEntry.triggeredBy();

            if (!triggeredBy.isEmpty() && !triggeredFields.containsKey(triggeredBy))
                throw new ElegantiaConfigException(
                        "Method `"
                                + Classes.getMethodView(method)
                                + "` indicates a dependency on trigger `"
                                + triggeredBy
                                + "`, which does not exist!"
                );

            entries.add(EntryInfo.action(method.getName(), triggeredBy, annotationEntry.translationKey(), () -> {
                try {
                    method.invoke(getInstance(configClass));
                } catch (IllegalAccessException e) {
                    throw new ElegantiaConfigException(
                            "Failed to execute method `"
                                    + Classes.getMethodView(method)
                                    + "` as an action because cannot be accessed"
                    );
                } catch (InvocationTargetException ignore) {}
            }));
        }

        CONFIGS_INFO.put(configClass, new ConfigInfo(
                modId,
                configInstance,
                entries,
                triggeredFields
        ));

        ModInfo.setConfigScreenGetter(modId, parent -> ConfigScreen.of(configClass, parent));

        return configInstance;
    }

    public static @Nullable ConfigInfo getConfigInfo(@NotNull Class<? extends Config> configClass) {
        return CONFIGS_INFO.get(configClass);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Config> @Nullable T getInstance(@NotNull Class<T> configClass) {
        if (!CONFIGS_INFO.containsKey(configClass))
            return null;

        return (T) CONFIGS_INFO.get(configClass).instance;
    }

    public static void forEach(@NotNull BiConsumer<@NotNull Class<? extends Config>, @NotNull ConfigInfo> consumer) {
        CONFIGS_INFO.forEach(consumer);
    }

    public record ConfigInfo(
            @NotNull String modId,
            @NotNull Config instance,
            @NotNull List<EntryInfo> entries,
            @NotNull HashMap<String, String> triggerFields
    ) {}

    public static class ElegantiaConfigException extends RuntimeException {
        public ElegantiaConfigException(@NotNull String message) {
            super(message);
        }
    }

    public static class ConfigTriggerException extends RuntimeException {
        public ConfigTriggerException(@NotNull String message) {
            super(message);
        }
    }
}
