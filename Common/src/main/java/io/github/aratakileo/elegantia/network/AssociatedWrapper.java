package io.github.aratakileo.elegantia.network;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.github.aratakileo.elegantia.core.Result;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class AssociatedWrapper<T extends Enum<T> & CodeAssociated> extends ResponseWrapper {
    private final static Cache<Class<?>, Map<Integer, ?>> STATUS_CACHE;

    private final T status;

    public AssociatedWrapper(@NotNull HttpResponse<String> response, @NotNull T defaultAssociation) {
        super(response);
        status = statusConstant(response.statusCode(), defaultAssociation);
    }

    public @NotNull T status() {
        return status;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T> & CodeAssociated> @NotNull T statusConstant(
            int statusCode,
            @NotNull T defaultAssociation
    ) {
        final var enumClass = (Class<T>)defaultAssociation.getClass();

        final var cacheSupplier = (Callable<Map<Integer, T>>) () -> {
            final var result = new HashMap<Integer, T>();

            for (final var constant : enumClass.getEnumConstants()) {
                final var code = constant.code();
                if (code != null) result.put(code, constant);
            }

            return result;
        };

        return Result.fromFactory(
                () -> ((Map<Integer, T>) STATUS_CACHE.get(enumClass, cacheSupplier))
                        .getOrDefault(statusCode, defaultAssociation)
        ).orElse(defaultAssociation);
    }

    static {
        STATUS_CACHE = CacheBuilder.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .maximumSize(100)
                .build();
    }
}
