package io.github.aratakileo.elegantia.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation in relation to the config field, which is indicated by annotation {@link ConfigEntry},
 * to indicate that the values of other fields that have the name of this trigger
 * specified in {@link ConfigEntry#triggeredBy()} may depend on the value of this field.
 * <br/>
 * <br/>
 * For example, if field one of the boolean type depends on field two of the boolean type,
 * then field one will be false if it is or if field two is false
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Trigger {
    /**
     * Name of trigger
     */
    String value();
}
