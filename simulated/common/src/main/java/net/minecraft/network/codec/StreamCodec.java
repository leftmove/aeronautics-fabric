package net.minecraft.network.codec;

import java.util.function.Function;

public interface StreamCodec<B, V> extends StreamDecoder<B, V>, StreamEncoder<B, V> {
    @Override
    V decode(B buf);

    @Override
    void encode(B buf, V value);

    static <B, V> StreamCodec<B, V> of(final StreamEncoder<B, V> encoder, final StreamDecoder<B, V> decoder) {
        return new StreamCodec<>() {
            @Override
            public V decode(final B buf) {
                return decoder.decode(buf);
            }

            @Override
            public void encode(final B buf, final V value) {
                encoder.encode(buf, value);
            }
        };
    }

    static <B, V> StreamCodec<B, V> ofMember(final StreamMemberEncoder<B, V> encoder, final StreamDecoder<B, V> decoder) {
        return of((buf, value) -> encoder.encode(value, buf), decoder);
    }

    static <B, V> StreamCodec<B, V> unit(final V value) {
        return of((buf, v) -> {}, buf -> value);
    }

    default <O> StreamCodec<B, O> map(final Function<? super V, ? extends O> from, final Function<? super O, ? extends V> to) {
        return of(
                (buf, value) -> encode(buf, to.apply(value)),
                buf -> from.apply(decode(buf))
        );
    }

    default <O> StreamCodec<B, O> apply(final CodecOperation<B, V, O> operation) {
        return operation.apply(this);
    }

    @FunctionalInterface
    interface CodecOperation<B, S, T> {
        StreamCodec<B, T> apply(StreamCodec<B, S> codec);
    }

    @FunctionalInterface
    interface StreamMemberEncoder<B, V> {
        void encode(V value, B buf);
    }

    @FunctionalInterface
    interface Function3<T1, T2, T3, R> {
        R apply(T1 t1, T2 t2, T3 t3);
    }

    @FunctionalInterface
    interface Function4<T1, T2, T3, T4, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4);
    }

    @FunctionalInterface
    interface Function5<T1, T2, T3, T4, T5, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5);
    }

    @FunctionalInterface
    interface Function6<T1, T2, T3, T4, T5, T6, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6);
    }

    @FunctionalInterface
    interface Function7<T1, T2, T3, T4, T5, T6, T7, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7);
    }

    @FunctionalInterface
    interface Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> {
        R apply(T1 t1, T2 t2, T3 t3, T4 t4, T5 t5, T6 t6, T7 t7, T8 t8);
    }

    static <B, C, T1> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final java.util.function.Function<T1, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            }
        };
    }

    static <B, C, T1, T2> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final java.util.function.BiFunction<T1, T2, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final Function3<T1, T2, T3, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final StreamCodec<? super B, T4> c4, final java.util.function.Function<C, T4> g4,
            final Function4<T1, T2, T3, T4, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf), c4.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            c4.encode(buf, g4.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final StreamCodec<? super B, T4> c4, final java.util.function.Function<C, T4> g4,
            final StreamCodec<? super B, T5> c5, final java.util.function.Function<C, T5> g5,
            final Function5<T1, T2, T3, T4, T5, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf), c4.decode(buf), c5.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            c4.encode(buf, g4.apply(value));
            c5.encode(buf, g5.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final StreamCodec<? super B, T4> c4, final java.util.function.Function<C, T4> g4,
            final StreamCodec<? super B, T5> c5, final java.util.function.Function<C, T5> g5,
            final StreamCodec<? super B, T6> c6, final java.util.function.Function<C, T6> g6,
            final Function6<T1, T2, T3, T4, T5, T6, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf), c4.decode(buf), c5.decode(buf), c6.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            c4.encode(buf, g4.apply(value));
            c5.encode(buf, g5.apply(value));
            c6.encode(buf, g6.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final StreamCodec<? super B, T4> c4, final java.util.function.Function<C, T4> g4,
            final StreamCodec<? super B, T5> c5, final java.util.function.Function<C, T5> g5,
            final StreamCodec<? super B, T6> c6, final java.util.function.Function<C, T6> g6,
            final StreamCodec<? super B, T7> c7, final java.util.function.Function<C, T7> g7,
            final Function7<T1, T2, T3, T4, T5, T6, T7, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf), c4.decode(buf), c5.decode(buf), c6.decode(buf), c7.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            c4.encode(buf, g4.apply(value));
            c5.encode(buf, g5.apply(value));
            c6.encode(buf, g6.apply(value));
            c7.encode(buf, g7.apply(value));
            }
        };
    }

    static <B, C, T1, T2, T3, T4, T5, T6, T7, T8> StreamCodec<B, C> composite(
            final StreamCodec<? super B, T1> c1, final java.util.function.Function<C, T1> g1,
            final StreamCodec<? super B, T2> c2, final java.util.function.Function<C, T2> g2,
            final StreamCodec<? super B, T3> c3, final java.util.function.Function<C, T3> g3,
            final StreamCodec<? super B, T4> c4, final java.util.function.Function<C, T4> g4,
            final StreamCodec<? super B, T5> c5, final java.util.function.Function<C, T5> g5,
            final StreamCodec<? super B, T6> c6, final java.util.function.Function<C, T6> g6,
            final StreamCodec<? super B, T7> c7, final java.util.function.Function<C, T7> g7,
            final StreamCodec<? super B, T8> c8, final java.util.function.Function<C, T8> g8,
            final Function8<T1, T2, T3, T4, T5, T6, T7, T8, C> factory) {
        return new StreamCodec<>() {
            @Override
            public C decode(final B buf) {
                return factory.apply(c1.decode(buf), c2.decode(buf), c3.decode(buf), c4.decode(buf), c5.decode(buf), c6.decode(buf), c7.decode(buf), c8.decode(buf));
            }

            @Override
            public void encode(final B buf, final C value) {
            c1.encode(buf, g1.apply(value));
            c2.encode(buf, g2.apply(value));
            c3.encode(buf, g3.apply(value));
            c4.encode(buf, g4.apply(value));
            c5.encode(buf, g5.apply(value));
            c6.encode(buf, g6.apply(value));
            c7.encode(buf, g7.apply(value));
            c8.encode(buf, g8.apply(value));
            }
        };
    }
}
