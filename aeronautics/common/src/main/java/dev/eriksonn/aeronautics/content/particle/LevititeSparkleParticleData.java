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

public class LevititeSparkleParticleData implements ParticleOptions, ICustomParticleDataWithSprite<LevititeSparkleParticleData> {
    public static final int LEVITITE_GREEN = 9424022;
    public static final int LEVITITE_PINK = 15521489;

    public static final MapCodec<LevititeSparkleParticleData> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.fieldOf("color").forGetter(p -> p.color)
            ).apply(instance, LevititeSparkleParticleData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, LevititeSparkleParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, p -> p.color,
            LevititeSparkleParticleData::new
    );

    public final int color;

    public LevititeSparkleParticleData(final int color) {
        this.color = color;
    }

    public LevititeSparkleParticleData() {
        this(LEVITITE_GREEN);
    }

    @Override
    public ParticleEngine.SpriteParticleRegistration<LevititeSparkleParticleData> getMetaFactory() {
        return LevititeSparkleParticle.Factory::new;
    }

    @Override
    public com.mojang.serialization.Codec<LevititeSparkleParticleData> getCodec(final ParticleType<LevititeSparkleParticleData> type) { return CODEC.codec(); }

    
    @Override
    public ParticleOptions.Deserializer<LevititeSparkleParticleData> getDeserializer() {
        return (type, buf) -> STREAM_CODEC.decode(buf);
    }

    @Override
    public void writeToNetwork(final net.minecraft.network.FriendlyByteBuf buffer) {
        STREAM_CODEC.encode(buffer, this);
    }

    @Override
    public String writeToString() {
        return this.getType().toString();
    }


    @Override
    public ParticleType<?> getType() {
        return AeroParticleTypes.LEVITITE_SPARKLE.get();
    }
}
