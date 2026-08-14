package net.minecraft.core.component;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class DataComponentPatch {
    public static final DataComponentPatch EMPTY = new DataComponentPatch(Map.of());
    public static final Codec<DataComponentPatch> CODEC = Codec.unit(EMPTY);

    private final Map<DataComponentType<?>, Optional<?>> values;

    private DataComponentPatch(final Map<DataComponentType<?>, Optional<?>> values) {
        this.values = values;
    }

    public Map<DataComponentType<?>, Optional<?>> asMap() {
        return this.values;
    }

    public java.util.Set<Map.Entry<DataComponentType<?>, Optional<?>>> entrySet() {
        return this.values.entrySet();
    }

    public boolean isEmpty() {
        return this.values.isEmpty();
    }

    public CompoundTag toTag() {
        final CompoundTag tag = new CompoundTag();
        this.values.forEach((type, optional) -> {
            if (type.codec() == null || type.getId() == null) {
                return;
            }
            if (optional.isPresent()) {
                encode(tag, type, optional.get());
            }
        });
        return tag;
    }

    @SuppressWarnings("unchecked")
    private static <T> void encode(final CompoundTag tag, final DataComponentType<T> type, final Object value) {
        final DataResult<Tag> result = type.codec().encodeStart(NbtOps.INSTANCE, (T) value);
        result.result().ifPresent(encoded -> tag.put(type.getId().toString(), encoded));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private final Map<DataComponentType<?>, Optional<?>> values = new HashMap<>();

        public <T> Builder set(final DataComponentType<T> type, final T value) {
            this.values.put(type, Optional.ofNullable(value));
            return this;
        }

        public Builder remove(final DataComponentType<?> type) {
            this.values.put(type, Optional.empty());
            return this;
        }

        public DataComponentPatch build() {
            return new DataComponentPatch(Map.copyOf(this.values));
        }
    }
}
