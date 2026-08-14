package net.minecraft.network.codec;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import java.util.UUID;

public final class StreamCodecs {
    public static final StreamCodec<ByteBuf, BlockPos> BLOCK_POS = StreamCodec.of(
            (buf, pos) -> friendly(buf).writeBlockPos(pos),
            buf -> friendly(buf).readBlockPos()
    );

    public static final StreamCodec<ByteBuf, ResourceLocation> RESOURCE_LOCATION = StreamCodec.of(
            (buf, id) -> friendly(buf).writeResourceLocation(id),
            buf -> friendly(buf).readResourceLocation()
    );

    public static final StreamCodec<ByteBuf, Direction> DIRECTION = StreamCodec.of(
            (buf, direction) -> friendly(buf).writeEnum(direction),
            buf -> friendly(buf).readEnum(Direction.class)
    );

    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodec.of(
            (buf, vec) -> {
                buf.writeDouble(vec.x);
                buf.writeDouble(vec.y);
                buf.writeDouble(vec.z);
            },
            buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
    );

    public static final StreamCodec<ByteBuf, Quaternionf> QUATERNIONF = StreamCodec.of(
            (buf, quat) -> friendly(buf).writeQuaternion(quat),
            buf -> friendly(buf).readQuaternion()
    );

    public static final StreamCodec<ByteBuf, UUID> UUID = StreamCodec.of(
            (buf, uuid) -> friendly(buf).writeUUID(uuid),
            buf -> friendly(buf).readUUID()
    );

    public static final StreamCodec<ByteBuf, net.minecraft.world.InteractionHand> HAND = StreamCodec.of(
            (buf, hand) -> friendly(buf).writeEnum(hand),
            buf -> friendly(buf).readEnum(net.minecraft.world.InteractionHand.class)
    );

    public static final StreamCodec<ByteBuf, GlobalPos> GLOBAL_POS = StreamCodec.of(
            (buf, pos) -> {
                friendly(buf).writeResourceLocation(pos.dimension().location());
                friendly(buf).writeBlockPos(pos.pos());
            },
            buf -> GlobalPos.of(
                    net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.DIMENSION, friendly(buf).readResourceLocation()),
                    friendly(buf).readBlockPos()
            )
    );

    public static final StreamCodec<ByteBuf, ItemStack> ITEM_STACK = StreamCodec.of(
            (buf, stack) -> friendly(buf).writeItem(stack),
            buf -> friendly(buf).readItem()
    );

    public static final StreamCodec<ByteBuf, ItemStack> OPTIONAL_ITEM_STACK = ITEM_STACK;

    public static <B, L, R> StreamCodec<B, com.mojang.datafixers.util.Pair<L, R>> pair(
            final StreamCodec<? super B, L> left,
            final StreamCodec<? super B, R> right
    ) {
        return StreamCodec.composite(left, com.mojang.datafixers.util.Pair::getFirst, right, com.mojang.datafixers.util.Pair::getSecond, com.mojang.datafixers.util.Pair::of);
    }

    public static <B, L, R> StreamCodec<B, net.createmod.catnip.data.Pair<L, R>> catnipPair(
            final StreamCodec<? super B, L> left,
            final StreamCodec<? super B, R> right
    ) {
        return StreamCodec.composite(left, net.createmod.catnip.data.Pair::getFirst, right, net.createmod.catnip.data.Pair::getSecond, net.createmod.catnip.data.Pair::of);
    }

    public static <T> StreamCodec<ByteBuf, net.minecraft.resources.ResourceKey<T>> resourceKey(
            final net.minecraft.resources.ResourceKey<? extends net.minecraft.core.Registry<T>> registry
    ) {
        return RESOURCE_LOCATION.map(
                id -> net.minecraft.resources.ResourceKey.create(registry, id),
                net.minecraft.resources.ResourceKey::location
        );
    }

    public static FriendlyByteBuf friendly(final ByteBuf buf) {
        return buf instanceof FriendlyByteBuf friendly ? friendly : new FriendlyByteBuf(buf);
    }
}
