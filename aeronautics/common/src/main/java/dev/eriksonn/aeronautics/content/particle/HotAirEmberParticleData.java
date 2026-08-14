package dev.eriksonn.aeronautics.content.particle;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class HotAirEmberParticleData implements ParticleOptions, ICustomParticleDataWithSprite<HotAirEmberParticleData> {

    private static final MapCodec<HotAirEmberParticleData> CODEC = RecordCodecBuilder.mapCodec((i) -> i.group(
                    Codec.BOOL.fieldOf("isSoul").forGetter((p) -> p.isSoul)
    ).apply(i, HotAirEmberParticleData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, HotAirEmberParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, (p) -> p.isSoul,
            HotAirEmberParticleData::new);

    protected final boolean isSoul;

    public HotAirEmberParticleData(final boolean isSoul) {
        this.isSoul = isSoul;
    }

    public HotAirEmberParticleData() {
        this.isSoul = false;
    }

    @Override
    public ParticleType<?> getType() {
        return AeroParticleTypes.HOT_AIR_EMBER.get();
    }

    @Override
    public ParticleEngine.SpriteParticleRegistration<HotAirEmberParticleData> getMetaFactory() {
        return HotAirEmberParticle.Factory::new;
    }

    @Override
    public com.mojang.serialization.Codec<HotAirEmberParticleData> getCodec(final ParticleType<HotAirEmberParticleData> particleType) { return CODEC.codec(); }

    
    @Override
    public ParticleOptions.Deserializer<HotAirEmberParticleData> getDeserializer() {
        return new ParticleOptions.Deserializer<>() {
            @Override
            public HotAirEmberParticleData fromCommand(final ParticleType<HotAirEmberParticleData> type, final com.mojang.brigadier.StringReader reader) {
                return new HotAirEmberParticleData();
            }

            @Override
            public HotAirEmberParticleData fromNetwork(final ParticleType<HotAirEmberParticleData> type, final net.minecraft.network.FriendlyByteBuf buf) {
                return new HotAirEmberParticleData(buf.readBoolean());
            }
        };
    }

    @Override
    public void writeToNetwork(final net.minecraft.network.FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.isSoul);
    }

    @Override
    public String writeToString() {
        return this.getType().toString();
    }

}
