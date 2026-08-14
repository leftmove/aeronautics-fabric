package dev.simulated_team.simulated.content.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.simulated_team.simulated.index.SimParticleTypes;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class MagnetFieldParticleData implements ParticleOptions, ICustomParticleDataWithSprite<MagnetFieldParticleData> {
    public static final MapCodec<MagnetFieldParticleData> CODEC = RecordCodecBuilder.mapCodec((i) -> {
        return i.group(Codec.BOOL.fieldOf("negative").forGetter((p) -> {
            return p.negative;
        })).apply(i, MagnetFieldParticleData::new);
    });
    public static final StreamCodec<ByteBuf, MagnetFieldParticleData> STREAM_CODEC;
    private boolean negative;

    public MagnetFieldParticleData(final boolean negative) {
        this.negative = negative;
    }

    public MagnetFieldParticleData() {
        this.negative = false;
    }

    public ParticleType<?> getType() {
        return SimParticleTypes.MAGNET_FIELD.get();
    }

    public com.mojang.serialization.Codec<MagnetFieldParticleData> getCodec(final ParticleType<MagnetFieldParticleData> type) { return CODEC.codec(); }

    public ParticleOptions.Deserializer<MagnetFieldParticleData> getDeserializer() {
        return new ParticleOptions.Deserializer<>() {
            @Override
            public MagnetFieldParticleData fromCommand(final ParticleType<MagnetFieldParticleData> type, final com.mojang.brigadier.StringReader reader) {
                return new MagnetFieldParticleData();
            }

            @Override
            public MagnetFieldParticleData fromNetwork(final ParticleType<MagnetFieldParticleData> type, final net.minecraft.network.FriendlyByteBuf buf) {
                return new MagnetFieldParticleData(buf.readBoolean());
            }
        };
    }

    public void writeToNetwork(final net.minecraft.network.FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.negative);
    }

    public String writeToString() {
        return Boolean.toString(this.negative);
    }

    public ParticleEngine.SpriteParticleRegistration<MagnetFieldParticleData> getMetaFactory() {
        return MagnetFieldParticle.Factory::new;
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, MagnetFieldParticleData> getStreamCodec() {
        return STREAM_CODEC;
    }

    static {
        STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL, (p) -> p.negative, MagnetFieldParticleData::new);
    }

    public boolean isNegative() {
        return this.negative;
    }

    public void setNegative(final boolean negative) {
        this.negative = negative;
    }
}

