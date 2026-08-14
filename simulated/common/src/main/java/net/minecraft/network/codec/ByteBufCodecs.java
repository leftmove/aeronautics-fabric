package net.minecraft.network.codec;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.NbtOps;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;

public final class ByteBufCodecs {
    public static final StreamCodec<ByteBuf, Boolean> BOOL = StreamCodec.of(ByteBuf::writeBoolean, ByteBuf::readBoolean);
    public static final StreamCodec<ByteBuf, Byte> BYTE = StreamCodec.of((buf, value) -> buf.writeByte(value), ByteBuf::readByte);
    public static final StreamCodec<ByteBuf, Short> SHORT = StreamCodec.of((buf, value) -> buf.writeShort(value), ByteBuf::readShort);
    public static final StreamCodec<ByteBuf, Integer> INT = StreamCodec.of(ByteBuf::writeInt, ByteBuf::readInt);
    public static final StreamCodec<ByteBuf, Integer> VAR_INT = StreamCodec.of(
            (buf, value) -> StreamCodecs.friendly(buf).writeVarInt(value),
            buf -> StreamCodecs.friendly(buf).readVarInt()
    );
    public static final StreamCodec<ByteBuf, Long> LONG = StreamCodec.of(ByteBuf::writeLong, ByteBuf::readLong);
    public static final StreamCodec<ByteBuf, Float> FLOAT = StreamCodec.of(ByteBuf::writeFloat, ByteBuf::readFloat);
    public static final StreamCodec<ByteBuf, Double> DOUBLE = StreamCodec.of(ByteBuf::writeDouble, ByteBuf::readDouble);
    public static final StreamCodec<ByteBuf, String> STRING_UTF8 = StreamCodec.of(
            (buf, value) -> StreamCodecs.friendly(buf).writeUtf(value),
            buf -> StreamCodecs.friendly(buf).readUtf()
    );
    public static final StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = StreamCodec.of(
            (buf, value) -> StreamCodecs.friendly(buf).writeByteArray(value),
            buf -> StreamCodecs.friendly(buf).readByteArray()
    );
    public static final StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = StreamCodec.of(
            (buf, value) -> StreamCodecs.friendly(buf).writeNbt(value),
            buf -> StreamCodecs.friendly(buf).readNbt()
    );
    public static final StreamCodec<ByteBuf, org.joml.Quaternionf> QUATERNIONF = StreamCodecs.QUATERNIONF;

    private ByteBufCodecs() {
    }

    public static <T> StreamCodec<ByteBuf, T> fromCodec(final Codec<T> codec) {
        return StreamCodec.of(
                (buf, value) -> {
                    final Tag tag = codec.encodeStart(NbtOps.INSTANCE, value).getOrThrow(false, s -> {
                    });
                    final CompoundTag compound = new CompoundTag();
                    compound.put("v", tag);
                    StreamCodecs.friendly(buf).writeNbt(compound);
                },
                buf -> {
                    final CompoundTag compound = StreamCodecs.friendly(buf).readNbt();
                    final Tag tag = compound == null ? new CompoundTag() : compound.get("v");
                    return codec.parse(NbtOps.INSTANCE, tag).getOrThrow(false, s -> {
                    });
                }
        );
    }

    public static <T> StreamCodec<ByteBuf, Optional<T>> optional(final StreamCodec<? super ByteBuf, T> codec) {
        return StreamCodec.of(
                (buf, optional) -> {
                    buf.writeBoolean(optional.isPresent());
                    optional.ifPresent(value -> codec.encode(buf, value));
                },
                buf -> buf.readBoolean() ? Optional.of(codec.decode(buf)) : Optional.empty()
        );
    }

    public static <T, C extends Collection<T>> StreamCodec<ByteBuf, C> collection(final IntFunction<C> factory, final StreamCodec<? super ByteBuf, T> codec) {
        return StreamCodec.of(
                (buf, collection) -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    friendly.writeVarInt(collection.size());
                    for (final T value : collection) {
                        codec.encode(buf, value);
                    }
                },
                buf -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    final int size = friendly.readVarInt();
                    final C collection = factory.apply(size);
                    for (int i = 0; i < size; i++) {
                        collection.add(codec.decode(buf));
                    }
                    return collection;
                }
        );
    }

    public static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(final IntFunction<M> factory, final StreamCodec<? super B, K> keyCodec, final StreamCodec<? super B, V> valueCodec) {
        return StreamCodec.of(
                (buf, map) -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    friendly.writeVarInt(map.size());
                    map.forEach((key, value) -> {
                        keyCodec.encode(buf, key);
                        valueCodec.encode(buf, value);
                    });
                },
                buf -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    final int size = friendly.readVarInt();
                    final M map = factory.apply(size);
                    for (int i = 0; i < size; i++) {
                        map.put(keyCodec.decode(buf), valueCodec.decode(buf));
                    }
                    return map;
                }
        );
    }

    public static <T> StreamCodec.CodecOperation<ByteBuf, T, List<T>> list() {
        return list(Integer.MAX_VALUE);
    }

    public static <T> StreamCodec.CodecOperation<ByteBuf, T, List<T>> list(final int maxSize) {
        return codec -> StreamCodec.of(
                (buf, list) -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    friendly.writeVarInt(list.size());
                    for (final T value : list) {
                        codec.encode(buf, value);
                    }
                },
                buf -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    final int size = friendly.readVarInt();
                    final List<T> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        list.add(codec.decode(buf));
                    }
                    return list;
                }
        );
    }
}
