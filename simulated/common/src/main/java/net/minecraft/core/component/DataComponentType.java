package net.minecraft.core.component;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.Registry;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class DataComponentType<T> {
    public static final ResourceKey<Registry<DataComponentType<?>>> REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation("simulated", "data_component_type"));

    private ResourceLocation id;
    private final Codec<T> codec;
    private final StreamCodec<? super ByteBuf, T> streamCodec;

    private DataComponentType(final Codec<T> codec, final StreamCodec<? super ByteBuf, T> streamCodec) {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    public ResourceLocation getId() {
        return this.id;
    }

    public Codec<T> codec() {
        return this.codec;
    }

    public StreamCodec<? super ByteBuf, T> streamCodec() {
        return this.streamCodec;
    }

    public void bind(final ResourceLocation id) {
        this.id = id;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }

    public static final class Builder<T> {
        private Codec<T> codec;
        private StreamCodec<? super ByteBuf, T> streamCodec;

        public Builder<T> persistent(final Codec<T> codec) {
            this.codec = codec;
            return this;
        }

        public Builder<T> networkSynchronized(final StreamCodec<? super ByteBuf, T> streamCodec) {
            this.streamCodec = streamCodec;
            return this;
        }

        public DataComponentType<T> build() {
            return new DataComponentType<>(this.codec, this.streamCodec);
        }
    }
}
