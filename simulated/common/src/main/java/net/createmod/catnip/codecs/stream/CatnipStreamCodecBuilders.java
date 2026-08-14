package net.createmod.catnip.codecs.stream;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamCodecs;

import java.util.ArrayList;
import java.util.List;

public final class CatnipStreamCodecBuilders {
    private CatnipStreamCodecBuilders() {
    }

    public static <T extends Enum<T>> StreamCodec<ByteBuf, T> ofEnum(final Class<T> type) {
        return StreamCodec.of(
                (buf, value) -> StreamCodecs.friendly(buf).writeEnum(value),
                buf -> StreamCodecs.friendly(buf).readEnum(type)
        );
    }

    public static <T> StreamCodec<ByteBuf, List<T>> list(final StreamCodec<? super ByteBuf, T> element) {
        return StreamCodec.of(
                (buf, values) -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    friendly.writeVarInt(values.size());
                    for (final T value : values) {
                        element.encode(buf, value);
                    }
                },
                buf -> {
                    final FriendlyByteBuf friendly = StreamCodecs.friendly(buf);
                    final int size = friendly.readVarInt();
                    final List<T> values = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        values.add(element.decode(buf));
                    }
                    return values;
                }
        );
    }
}
