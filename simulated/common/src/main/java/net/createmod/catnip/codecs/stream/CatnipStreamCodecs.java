package net.createmod.catnip.codecs.stream;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.StreamCodecs;
import net.minecraft.world.phys.Vec3;

public final class CatnipStreamCodecs {
    public static final StreamCodec<ByteBuf, Vec3> VEC3 = StreamCodecs.VEC3;
    public static final StreamCodec<ByteBuf, net.minecraft.core.BlockPos> BLOCK_POS = StreamCodecs.BLOCK_POS;
    public static final StreamCodec<ByteBuf, net.minecraft.resources.ResourceLocation> RESOURCE_LOCATION = StreamCodecs.RESOURCE_LOCATION;

    private CatnipStreamCodecs() {
    }
}
