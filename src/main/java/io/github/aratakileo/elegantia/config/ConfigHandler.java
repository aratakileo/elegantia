package io.github.aratakileo.elegantia.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.aratakileo.elegantia.core.ModInfo;
import io.github.aratakileo.elegantia.core.Namespace;
import io.github.aratakileo.elegantia.gui.screen.ConfigScreen;
import io.github.aratakileo.elegantia.util.Classes;
import io.github.aratakileo.elegantia.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class ConfigHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ConfigHandler.class);

    private final static Gson GSON = new GsonBuilder()
            .setFieldNamingStrategy(field -> Strings.camelToSnake(field.getName()))
            .setPrettyPrinting()
            .create();

    private final static Pattern TRIGGER_NAME_PATTERN = Pattern.compile("^(?![0-9-]+)[A-Za-z0-9_-]+[^-]$");

    private final static ConcurrentHashMap<Class<? extends AbstractConfig>, AbstractConfig> CONFIG_INSTANCES = new ConcurrentHashMap<>();

    protected static <T extends AbstractConfig> @Nullable T newInstance(@NotNull Class<T> configClass) {
        try {
            return configClass.getConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.error("Failed to create a new instance of `" + configClass.getName() + "`: ", e);
        }

        return null;
    }

    private static <T extends AbstractConfig> @Nullable T load(@NotNull Class<T> configClass, @NotNull File file) {
        if (file.exists()) {
            try {
                final var fileReader = new FileReader(file);
                final var configInstance = GSON.fromJson(fileReader, configClass);

                fileReader.close();

                return configInstance;
            } catch (Exception e) {
                LOGGER.error("Failed to load config by path `" + file.getPath() + "`: ", e);
            }
        }

        return null;
    }

    protected static void save(@NotNull AbstractConfig configInstance, @NotNull File file) {
        final var parentFile = file.getParentFile();

        if (!parentFile.exists() && !parentFile.mkdir())
            LOGGER.warn(
                    "Failed to make dir `"
                            + parentFile.getPath()
                            + "`. Possible reason: insufficient permissions to perform this action"
            );

        try {
            final var fileWriter = new FileWriter(file);
            fileWriter.write(GSON.toJson(configInstance));
            fileWriter.close();
        } catch (Exception e) {
            LOGGER.error("Failed to save config by path `" + file.getPath() + "`: ", e);
        }
    }

    protected static @Nullable File getConfigFile(@NotNull Class<? extends AbstractConfig> configClass) {
        if (!CONFIG_INSTANCES.containsKey(configClass))
            return null;

        return getConfigFile(CONFIG_INSTANCES.get(configClass).getNamespace());
    }

    /**
     * @return Theoretically possible config file for the specified mod id
     */
    private static @NotNull File getConfigFile(@NotNull Namespace namespace) {
        return new File("config/" + namespace.get() + ".json");
    }

    @SuppressWarnings("UnusedReturnValue")
    public static <T extends AbstractConfig> @Nullable T init(@NotNull Class<T> configClass, @NotNull Namespace namespace) {
        final var configClassModifiers = configClass.getModifiers();

        if (Modifier.isAbstract(configClassModifiers))
            throw new InvalidConfigClassException("%s is abstract".formatted(configClass.getName()));

        if (Modifier.isInterface(configClassModifiers))
            throw new InvalidConfigClassException("%s is interface".formatted(configClass.getName()));

        if (Modifier.isPrivate(configClassModifiers) || Modifier.isProtected(configClassModifiers))
            throw new InvalidConfigClassException("%s must be public".formatted(configClass.getName()));

        if (CONFIG_INSTANCES.containsKey(configClass)) {
            LOGGER.warn("Config `{}` is already inited!", configClass.getName());
            return null;
        }

        ModInfo.getOrThrow(namespace);

        final var configInstance = Optional.ofNullable(load(
                configClass,
                getConfigFile(namespace)
        )).orElse(newInstance(configClass));

        if (configInstance == null) {
            LOGGER.warn("Failed to init config `{}`", configClass.getName());
            return null;
        }

        final var entryFields = new ArrayList<Field>();

        configInstance.namespaceLateInit.set(namespace);

        for (final var field: configClass.getDeclaredFields()) {
            final var isTrigger = field.isAnnotationPresent(Trigger.class);

            if (!field.isAnnotationPresent(ConfigEntry.class)) {
                if (isTrigger)
                    LOGGER.warn(
                            "Field `"
                                    + Classes.getFieldView(field)
                                    + "` is not marked with the @ConfigEntry annotation "
                                    + "which means that this field will not be displayed "
                                    + "on the mod configuration screen"
                    );

                continue;
            }

            if (field.getType() != boolean.class) {
                LOGGER.warn(
                        "Field `{}` has unsupported config field type. {}",
                        Classes.getFieldView(field),
                        "This field will not be displayed on the config screen"
                );
                continue;
            }

            final var fieldModifiers = field.getModifiers();

            if (Modifier.isPrivate(fieldModifiers)){
                LOGGER.warn(
                        "Field `{}` is private. {}",
                        Classes.getFieldView(field),
                        "This field will not be displayed on the config screen"
                );
                continue;
            }

            if (Modifier.isProtected(fieldModifiers)){
                LOGGER.warn(
                        "Field `{}` is protected. {}",
                        Classes.getFieldView(field),
                        "This field will not be displayed on the config screen"
                );
                continue;
            }

            entryFields.add(field);

            if (isTrigger) {
                final var triggerName = field.getAnnotation(Trigger.class).value();

                if (!TRIGGER_NAME_PATTERN.matcher(triggerName).find()) throw new InvalidTriggerException(
                        "Trigger name `%s` does not match the pattern `%s` (trigger field: %s)!".formatted(
                                triggerName,
                                TRIGGER_NAME_PATTERN,
                                Classes.getFieldView(field)
                        )
                );

                if (!field.getAnnotation(ConfigEntry.class).triggeredBy().isEmpty()) throw new InvalidTriggerException(
                        "Trigger cannot be triggered (trigger field: %s)!".formatted(Classes.getFieldView(field))
                );

                configInstance.triggerFields.put(triggerName, field.getName());
            }
        }

        for (final var field: entryFields) {
            final var entryAnnotation = field.getAnnotation(ConfigEntry.class);
            final var triggeredBy = entryAnnotation.triggeredBy();

            if (!triggeredBy.isEmpty() && !configInstance.triggerFields.containsKey(triggeredBy))
                throw new InvalidConfigFieldException(
                        "Field `%s` indicates a dependency on trigger `%S`, which does not exist!".formatted(
                                Classes.getFieldView(field),
                                triggeredBy
                        )
                );

            configInstance.entries.add(EntryInfo.value(
                    EntryInfo.Type.BOOLEAN,
                    field.getName(),
                    field.isAnnotationPresent(Trigger.class) ? field.getAnnotation(Trigger.class).value() : null,
                    triggeredBy,
                    entryAnnotation.translationKey()
            ));
        }

        for (final var method: configClass.getMethods()) {
            if (!method.isAnnotationPresent(ConfigEntry.class)) continue;

            if (method.getReturnType() != void.class) throw new InvalidActionException(
                    "Method `%s` cannot be represented as an action because %s".formatted(
                            Classes.getMethodView(method),
                            "it has a return type other than `void`"
                    )
            );

            if (method.getParameterCount() != 0) throw new InvalidActionException(
                    "Method `%s` cannot be represented as an action because it has parameters".formatted(
                            Classes.getMethodView(method)
                    )
            );

            final var entryAnnotation = method.getAnnotation(ConfigEntry.class);
            final var triggeredBy = entryAnnotation.triggeredBy();

            if (!triggeredBy.isEmpty() && !configInstance.triggerFields.containsKey(triggeredBy))
                throw new InvalidActionException(
                        "Method `%s` indicates a dependency on trigger `%s`, which does not exist!".formatted(
                                Classes.getMethodView(method),
                                triggeredBy
                        )
                );

            configInstance.entries.add(EntryInfo.action(
                    method.getName(),
                    triggeredBy,
                    entryAnnotation.translationKey(),
                    () -> {
                        try {
                            method.invoke(getInstance(configClass));
                        } catch (IllegalAccessException e) {
                            throw new FailedActionExecutionException("Method `%s` cannot be accessed".formatted(
                                    Classes.getMethodView(method)
                            ), e);
                        } catch (InvocationTargetException e) {
                            throw new FailedActionExecutionException("Method `%s`".formatted(
                                    Classes.getMethodView(method)
                            ), e);
                        }
                    },
                    method.isAnnotationPresent(InfluentialAction.class)
            ));
        }

        CONFIG_INSTANCES.put(configClass, configInstance);

        if (!configInstance.entries.isEmpty())
            ModInfo.setConfigScreenGetter(namespace, parent -> ConfigScreen.of(configClass, parent));

        return configInstance;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractConfig> @Nullable T getInstance(@NotNull Class<T> configClass) {
        final var configInstance = CONFIG_INSTANCES.get(configClass);

        return Objects.nonNull(configInstance) ? (T) configInstance : null ;
    }

    public static void setValuesByDefault(@NotNull Class<? extends AbstractConfig> configClass) {
        setValuesByDefault(configClass, false);
    }

    public static void setValuesByDefault(@NotNull Class<? extends AbstractConfig> configClass, boolean onlyConfigEntryFields) {
        if (!CONFIG_INSTANCES.containsKey(configClass)) return;

        final var newInstance = newInstance(configClass);

        if (Objects.isNull(newInstance)) {
            LOGGER.warn("Failed to set values by default for `{}`", configClass.getName());
            return;
        }

        final var currentInstance = CONFIG_INSTANCES.get(configClass);

        try {
            for (final var field : currentInstance.getClass().getDeclaredFields()) {
                if (
                        Modifier.isTransient(field.getModifiers())
                                || onlyConfigEntryFields && !field.isAnnotationPresent(ConfigEntry.class)
                ) continue;

                field.setAccessible(true);
                field.set(currentInstance, field.get(newInstance));
            }
        } catch (IllegalAccessException e) {
            LOGGER.warn("Failed to set values by default for `%s`".formatted(configClass.getName()), e);
        }
    }

    public static class InvalidConfigClassException extends RuntimeException {
        public InvalidConfigClassException(@NotNull String message) {
            super(message);
        }
    }

    public static class InvalidConfigFieldException extends RuntimeException {
        public InvalidConfigFieldException(@NotNull String message) {
            super(message);
        }
    }

    public static class InvalidActionException extends RuntimeException {
        public InvalidActionException(@NotNull String message) {
            super(message);
        }
    }

    public static class FailedActionExecutionException extends RuntimeException {
        public FailedActionExecutionException(@NotNull String message) {
            super(message);
        }

        public FailedActionExecutionException(@NotNull String message, @NotNull Throwable cause) {
            super(message, cause);
        }
    }
}
