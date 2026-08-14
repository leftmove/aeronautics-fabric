package net.minecraft.core.component;

import java.util.HashMap;
import java.util.Map;

public final class PatchedDataComponentMap implements DataComponentMap {
    private final DataComponentMap prototype;
    private final Map<DataComponentType<?>, Object> patch = new HashMap<>();

    public PatchedDataComponentMap(final DataComponentMap prototype) {
        this.prototype = prototype == null ? DataComponentMap.EMPTY : prototype;
    }

    public void applyPatch(final DataComponentPatch patch) {
        for (final var entry : patch.entrySet()) {
            if (entry.getValue().isPresent()) {
                this.patch.put(entry.getKey(), entry.getValue().get());
            } else {
                this.patch.put(entry.getKey(), null);
            }
        }
    }

    public <T> void set(final TypedDataComponent<T> component) {
        this.patch.put(component.type(), component.value());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(final DataComponentType<T> type) {
        if (this.patch.containsKey(type)) {
            return (T) this.patch.get(type);
        }
        return this.prototype.get(type);
    }

    public DataComponentMap toImmutableMap() {
        final DataComponentMap.Builder builder = DataComponentMap.builder();
        this.patch.forEach((type, value) -> {
            if (value != null) {
                setUnchecked(builder, type, value);
            }
        });
        return builder.build();
    }

    @SuppressWarnings("unchecked")
    private static <T> void setUnchecked(final DataComponentMap.Builder builder, final DataComponentType<?> type, final Object value) {
        builder.set((DataComponentType<T>) type, (T) value);
    }
}
