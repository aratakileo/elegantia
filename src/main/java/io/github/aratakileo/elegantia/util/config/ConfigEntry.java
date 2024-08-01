package io.github.aratakileo.elegantia.util.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation identifies fields or methods (as an action) of the config class
 * that should be displayed on the config screen.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface ConfigEntry {
    /**
     * Contains the translation key, which is used as follows:
     * `key` in `mod_id.config.entry.key.title` and `mod_id.config.entry.key.description`
     */
    String translationKey() default "";

    /**
     * Contains the names of {@link Trigger}
     */
    String triggeredBy() default "";
}
