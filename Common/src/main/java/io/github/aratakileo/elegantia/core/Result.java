package io.github.aratakileo.elegantia.core;

import io.github.aratakileo.elegantia.core.environment.Origin;
import io.github.aratakileo.elegantia.util.Arrays;
import io.github.aratakileo.elegantia.util.Exceptions;
import io.github.aratakileo.elegantia.util.Strings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class Result<T> {
    private final T ok;
    private final Throwable err;

    private Result(@Nullable T ok, @Nullable Throwable err) {
        this.ok = ok;
        this.err = err;
    }

    public boolean isOkay() {
        return ok != null;
    }

    public boolean isError() {
        return err != null;
    }

    public @NotNull T unwrap() {
        if (err != null) Exceptions.throwIt(err);
        return Objects.requireNonNull(ok);
    }

    public @NotNull Throwable unwrapError() {
        return Objects.requireNonNull(err);
    }

    public <R> @NotNull Result<R> map(@NotNull ThrowableMapper<T, R> mapper) {
        try {
            return isOkay() ? fromOk(mapper.map(ok)) : fromError(Objects.requireNonNull(err));
        } catch (Exception e) {
            return fromError(e);
        }
    }

    @SuppressWarnings("unchecked")
    public <U> Result<U> flatMap(@NotNull Function<? super T, ? extends Result<? extends U>> mapper) {
        if (err != null) return Result.fromError(err);

        return Objects.requireNonNull((Result<U>) mapper.apply(ok));
    }

    public @NotNull Result<T> logIfError(@NotNull Origin origin) {
        return logIfError(origin.logger());
    }

    public @NotNull Result<T> logIfError(
            @NotNull Origin origin,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        return logIfError(origin.logger(), message, args);
    }

    public @NotNull Result<T> logIfError(@NotNull Logger logger) {
        if (err != null) logger.error("Failed to get result: ", err);
        return this;
    }

    public @NotNull Result<T> logIfError(
            @NotNull Logger logger,
            @NotNull String message,
            @Nullable Object @NotNull... args
    ) {
        if (err == null) return this;

        if (args.length == 0) logger.error(message, err);
        else logger.error(message, Arrays.expandWithNullable(args, err));

        return this;
    }

    public @NotNull Result<T> throwOrLogIfError(@NotNull Origin origin) {
        Exceptions.throwOrLogIf(isError(), origin, () -> err);
        return this;
    }

    public @NotNull Result<T> throwOrLogIfError(@NotNull Logger logger) {
        Exceptions.throwOrLogIf(isError(), logger, () -> err);
        return this;
    }

    public @NotNull Result<T> throwOrLogIfError(@NotNull String loggerName) {
        Exceptions.throwOrLogIf(isError(), loggerName, () -> err);
        return this;
    }

    public @NotNull Result<T> runIfOkay(@NotNull Consumer<T> consumer) {
        if (ok != null) consumer.accept(ok);
        return this;
    }

    public @NotNull Result<T> runIfOkay(@NotNull Runnable runnable) {
        if (ok != null) runnable.run();
        return this;
    }

    public @NotNull T orElse(@NotNull T elseValue) {
        return ok == null ? elseValue : ok;
    }

    public static <T> @NotNull Result<T> fromOk(@NotNull T ok) {
        if (Objects.requireNonNull(ok) instanceof Optional<?>)
            throw new IllegalArgumentException(String.format("optional value `%s`", ok));

        return new Result<>(ok, null);
    }

    public static <T> @NotNull Result<T> fromError(@NotNull Throwable error) {
        return new Result<>(null, Objects.requireNonNull(error));
    }

    public static <T> @NotNull Result<T> fromError(@NotNull Supplier<? extends Throwable> exceptionFactory) {
        return new Result<>(null, Objects.requireNonNull(exceptionFactory.get()));
    }

    public static <T> @NotNull Result<T> fromError(
            @NotNull Function<String, ? extends Throwable> exceptionFactory,
            @NotNull String exceptionMessage,
            @Nullable Object @NotNull... messageArgs
    ) {
        final var formattedMessage = Strings.format(exceptionMessage, messageArgs);

        return new Result<>(null, Objects.requireNonNull(exceptionFactory.apply(formattedMessage)));
    }

    public static <T> @NotNull Result<T> fromFactory(@NotNull Result.ThrowableFactory<T> factory) {
        try {
            return Result.fromOk(factory.get());
        } catch (Exception e) {
            return Result.fromError(e);
        }
    }

    public static <T> @NotNull Result<T> fromNullable(
            @Nullable T value,
            @NotNull Supplier<? extends Throwable> exceptionFactory
    ) {
        return value == null ? Result.fromError(exceptionFactory.get()) : Result.fromOk(value);
    }

    public static <T> @NotNull Result<T> fromNullable(
            @Nullable T value,
            @NotNull Function<String, ? extends Throwable> exceptionFactory,
            @NotNull String exceptionMessage,
            @Nullable Object @NotNull... messageArgs
    ) {
        final var formattedMessage = Strings.format(exceptionMessage, messageArgs);

        return value == null
                ? Result.fromError(exceptionFactory.apply(formattedMessage))
                : Result.fromOk(value);
    }

    public static <T> @NotNull Result<T> fromOptional(
            @NotNull Optional<T> value,
            @NotNull Supplier<? extends Throwable> exceptionFactory
    ) {
        return value.map(Result::fromOk).orElseGet(() -> Result.fromError(exceptionFactory.get()));
    }

    public static <T> @NotNull Result<T> fromOptional(
            @NotNull Optional<T> value,
            @NotNull Function<String, ? extends Throwable> exceptionFactory,
            @NotNull String exceptionMessage,
            @Nullable Object @NotNull... messageArgs
    ) {
        final var formattedMessage = Strings.format(exceptionMessage, messageArgs);
        return value.map(Result::fromOk).orElseGet(() -> Result.fromError(exceptionFactory.apply(formattedMessage)));
    }

    @FunctionalInterface
    public interface ThrowableFactory<T> {
        @NotNull T get() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowableMapper<T, R> {
        @NotNull R map(@NotNull T input) throws Exception;
    }
}
