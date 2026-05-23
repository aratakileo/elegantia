package io.github.aratakileo.elegantia.util;

import io.github.aratakileo.elegantia.core.environment.Origin;
import net.minecraft.SharedConstants;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.function.Supplier;

public final class Exceptions {
    private Exceptions() {}

    public static void throwOrLog(
            @NotNull Origin origin,
            @NotNull Supplier<Throwable> exceptionFactory
    ) {
        throwOrLog(origin.logger(), exceptionFactory);
    }

    public static void throwOrLog(
            @NotNull Logger logger,
            @NotNull Supplier<Throwable> exceptionFactory
    ) {
        final var exception = exceptionFactory.get();
        if (SharedConstants.IS_RUNNING_IN_IDE) throwIt(exception);
        logger.error("Critical error occurred! Please report this to the mod developers.", exception);
    }

    public static void throwOrLog(
            @NotNull Origin origin,
            @NotNull Function<String, Throwable> exceptionFactory,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        throwOrLog(origin.logger(), exceptionFactory, message, args);
    }

    public static void throwOrLog(
            @NotNull Logger logger,
            @NotNull Function<String, Throwable> exceptionFactory,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        final var exception = exceptionFactory.apply(Strings.format(message, args));
        if (SharedConstants.IS_RUNNING_IN_IDE) throwIt(exception);
        logger.error("Critical error occurred! Please report this to the mod developers.", exception);
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull Origin origin,
            @NotNull Supplier<Throwable> exceptionFactory
    ) {
        if (condition) throwOrLog(origin.logger(), exceptionFactory);
        return condition;
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull Logger logger,
            @NotNull Supplier<Throwable> exceptionFactory
    ) {
        if (condition) throwOrLog(logger, exceptionFactory);
        return condition;
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull String loggerName,
            @NotNull Supplier<Throwable> exceptionFactory
    ) {
        if (condition) throwOrLog(LoggerFactory.getLogger(loggerName), exceptionFactory);
        return condition;
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull Origin origin,
            @NotNull Function<String, Throwable> exceptionFactory,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        if (condition) throwOrLog(origin, exceptionFactory, message, args);
        return condition;
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull Logger logger,
            @NotNull Function<String, Throwable> exceptionFactory,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        if (condition) throwOrLog(logger, exceptionFactory, message, args);
        return condition;
    }

    public static boolean throwOrLogIf(
            boolean condition,
            @NotNull String loggerName,
            @NotNull Function<String, Throwable> exceptionFactory,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        if (condition) throwOrLog(LoggerFactory.getLogger(loggerName), exceptionFactory, message, args);
        return condition;
    }

    public static void throwIt(@NotNull Throwable throwable) {
        if (throwable instanceof RuntimeException exception)
            throw exception;

        throw new RuntimeException(throwable);
    }
}
