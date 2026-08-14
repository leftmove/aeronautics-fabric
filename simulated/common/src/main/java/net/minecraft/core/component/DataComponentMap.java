package net.minecraft.core.component;

import java.util.HashMap;
import java.util.Map;

public interface DataComponentMap {
    DataComponentMap EMPTY = new Builder().build();

    <T> T get(DataComponentType<T> type);

    default boolean has(final DataComponentType<?> type) {
        return this.get(type) != null;
    }

    default <T> T getOrDefault(final DataComponentType<T> type, final T fallback) {
        final T value = this.get(type);
        return value != null ? value : fallback;
    }

    static Builder builder() {
        return new Builder();
    }

    final class Builder implements DataComponentMap {
        private final Map<DataComponentType<?>, Object> values = new HashMap<>();

        public <T> Builder set(final DataComponentType<T> type, final T value) {
            if (value == null) {
                this.values.remove(type);
            } else {
                this.values.put(type, value);
            }
            return this;
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T get(final DataComponentType<T> type) {
            return (T) this.values.get(type);
        }

        public DataComponentMap build() {
            final Map<DataComponentType<?>, Object> copy = Map.copyOf(this.values);
            return new DataComponentMap() {
                @SuppressWarnings("unchecked")
                @Override
                public <T> T get(final DataComponentType<T> type) {
                    return (T) copy.get(type);
                }
            };
        }
    }
}
