package io.github.aratakileo.elegantia.client.config;

import io.github.aratakileo.elegantia.client.gui.screen.ConfigScreen;
import io.github.aratakileo.elegantia.util.*;
import io.github.aratakileo.elegantia.util.type.LateInitedConst;
import io.github.aratakileo.elegantia.core.Namespace;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public abstract class AbstractConfig {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractConfig.class);

    protected final transient @NotNull LateInitedConst<Namespace> namespaceLateInit = new LateInitedConst<>();
    protected final transient @NotNull ArrayList<EntryInfo> entries = new ArrayList<>();
    protected final transient @NotNull HashMap<String, String> triggerFields = new HashMap<>();

    public boolean getTriggerValue(@NotNull String trigger) {
        final var triggerFields = Objects.requireNonNull(ConfigHandler.getInstance(getClass())).triggerFields;

        if (!triggerFields.containsKey(trigger))
            throw new InvalidTriggerException("Trigger `%s` does not exist".formatted(trigger));

        return getBooleanFieldValue(triggerFields.get(trigger));
    }

    /**
     * Sets the value of the specified field as it was before config initialization
     */
    public void setFieldDefaultValue(@NotNull String fieldName) {
        final var newInstance = ConfigHandler.newInstance(getClass());

        if (newInstance != null) {
            setFieldValue(fieldName, newInstance.getFieldValue(fieldName));
            return;
        }

        LOGGER.warn(
                "Failed to set config field `{}` with value by default",
                Classes.getFieldOrMethodView(getClass(), fieldName)
        );
    }

    public void setFieldValue(@NotNull String fieldName, @Nullable Object value) {
        try {
            getClass().getField(fieldName).set(this, value);
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "Failed to set config field `%s`".formatted(Classes.getFieldOrMethodView(getClass(), fieldName)),
                    e
            );
        } catch (NoSuchFieldException e) {
            LOGGER.error(
                    "Failed to set a non-existent config field `%s`".formatted(Classes.getFieldOrMethodView(
                            getClass(),
                            fieldName)
                    ),
                    e
            );
        }
    }

    public @Nullable Object getFieldValue(@NotNull String fieldName) {
        try {
            return getClass().getField(fieldName).get(this);
        } catch (IllegalAccessException e) {
            LOGGER.warn(
                    "Failed to get config field `%s`".formatted(Classes.getFieldOrMethodView(getClass(), fieldName)),
                    e
            );
        } catch (NoSuchFieldException e) {
            LOGGER.error(
                    "Failed to get a non-existent config field `%s`".formatted(Classes.getFieldOrMethodView(
                            getClass(),
                            fieldName
                    )),
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

    public void setValuesByDefault() {
        ConfigHandler.setValuesByDefault(getClass());
    }

    public void setValuesByDefault(boolean onlyConfigEntryFields) {
        ConfigHandler.setValuesByDefault(getClass(), onlyConfigEntryFields);
    }

    public void save() {
        ConfigHandler.save(this, Objects.requireNonNull(ConfigHandler.getConfigFile(this.getClass())));
    }

    public @Nullable ConfigScreen getScreen() {
        return ConfigScreen.of(getClass());
    }

    public @Nullable ConfigScreen getScreen(@NotNull Screen parent) {
        return ConfigScreen.of(getClass(), parent);
    }

    public @NotNull Namespace getNamespace() {
        return namespaceLateInit.asOptional().orElseThrow(() -> new IllegalStateException(
                "No namespace value present for instance of " + getClass().getName()
        ));
    }

    public @NotNull ArrayList<EntryInfo> getEntries() {
        return entries;
    }

    public @NotNull HashMap<String, String> getTriggerFields() {
        return triggerFields;
    }
}
