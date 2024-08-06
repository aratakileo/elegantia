package io.github.aratakileo.elegantia.core;

import io.github.aratakileo.elegantia.config.AbstractConfig;
import io.github.aratakileo.elegantia.world.ContainerAutoData;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation identifies fields or methods of {@link AbstractConfig} or {@link ContainerAutoData}
 * that should be ignored
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface Ignore {}
