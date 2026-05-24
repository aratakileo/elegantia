package io.github.aratakileo.elegantia.core;

import io.github.aratakileo.elegantia.core.util.Strings;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * A thread-safe, high-performance lazy value initializer utilizing
 * the Double-Checked Locking pattern. Safely supports and memorizes {@code null} values.
 *
 * @param <T> the type of the memoized value
 */
@ApiStatus.Experimental
public final class ThreadSafeInitializer<T> {
    private final Supplier<? extends T> factory;
    private volatile Object value = NO_VALUE;
    private static final Object NO_VALUE = new Object();

    private ThreadSafeInitializer(@NotNull Supplier<? extends T> factory) {
        this.factory = Objects.requireNonNull(factory);
    }

    /**
     * Unwraps the memoized value, asserting that it is not null,
     * providing a lazily formatted custom error message if it is.
     *
     * @param message the error message template (e.g. "failed for {}")
     * @param args arguments to be formatted into the template
     * @return the initialized value
     * @throws NullPointerException if the computed value is null
     */
    public @NotNull T unwrap(@NotNull String message, @Nullable Object @NotNull... args) {
        return Objects.requireNonNull(raw(), () -> Strings.format(message, args));
    }

    /**
     * Unwraps the memoized value, asserting that it is not null.
     * Generates a default error message lazily if the assertion fails.
     *
     * @return the initialized value
     * @throws NullPointerException if the computed value is null
     */
    public @NotNull T unwrap() {
        return Objects.requireNonNull(
                raw(),
                Strings.format("{} contains a null value!", ThreadSafeInitializer.class.getSimpleName())
        );
    }

    public @NotNull Optional<T> optional() {
        return Optional.ofNullable(raw());
    }

    @SuppressWarnings("unchecked")
    private @Nullable T raw() {
        /*
         * [FIRST CHECK / FAST PATH]
         * Read the volatile field into a local variable to query RAM exactly ONCE.
         * If the state is anything other than NO_VALUE (including a cached null),
         * we completely bypass synchronization and return the cached reference immediately.
         */
        var localValue = value;
        if (localValue != NO_VALUE) return (T) localValue;

        /*
         * Synchronize on this specific instance lock to queue concurrent threads.
         */
        synchronized (this) {
            /*
             * [SECOND CHECK / DOUBLE-CHECK]
             * Re-read the volatile field inside the locked monitor. This prevents
             * a waiting thread from re-executing the factory if a previous thread
             * has just completed the initialization phase while we were in queue.
             */
            localValue = value;

            if (localValue == NO_VALUE) {
                /*
                 * Execute the factory. The returned value is allowed to be null.
                 */
                localValue = factory.get();
                /*
                 * Publish the computed state (or null reference) to the volatile field,
                 * making it safely visible to all other threads via memory barriers.
                 */
                value = localValue;
            }

            /*
             * Return the local variable reference, which is extremely fast as it resides
             * in the CPU register / L1 cache rather than forcing another volatile RAM read.
             */
            return (T) localValue;
        }
    }

    public boolean initialized() {
        return value != NO_VALUE;
    }

    public static <T> @NotNull ThreadSafeInitializer<T> from(@NotNull Supplier<? extends T> factory) {
        return new ThreadSafeInitializer<>(factory);
    }
}