package io.github.aratakileo.elegantia.util.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation in relation to the config method, which is indicated by annotation {@link ConfigEntry},
 * which modifies the fields displayed on the config screen.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface InfluentialAction {
}
