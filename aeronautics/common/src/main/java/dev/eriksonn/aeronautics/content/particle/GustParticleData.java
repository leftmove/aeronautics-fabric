package dev.eriksonn.aeronautics.content.particle;

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
import net.minecraft.util.ExtraCodecs;
import org.joml.Quaternionf;

public record GustParticleData(
        Quaternionf orientation) implements ParticleOptions, ICustomParticleDataWithSprite<GustParticleData> {

    private static final MapCodec<GustParticleData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ExtraCodecs.QUATERNIONF.fieldOf("orientation").forGetter(o -> o.orientation)
    ).apply(instance, GustParticleData::new));

    private static final StreamCodec<RegistryFriendlyByteBuf, GustParticleData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.QUATERNIONF, (o -> o.orientation),
            GustParticleData::new
    );

    public GustParticleData() {
        this(new Quaternionf());
    }


    @Override
    public ParticleEngine.SpriteParticleRegistration<GustParticleData> getMetaFactory() {
        return GustParticle.Factory::new;
    }

    @Override
    public com.mojang.serialization.Codec<GustParticleData> getCodec(final ParticleType<GustParticleData> type) { return CODEC.codec(); }

    
    @Override
    public ParticleOptions.Deserializer<GustParticleData> getDeserializer() {
        return new ParticleOptions.Deserializer<>() {
            @Override
            public GustParticleData fromCommand(final ParticleType<GustParticleData> type, final com.mojang.brigadier.StringReader reader) {
                return new GustParticleData();
            }

            @Override
            public GustParticleData fromNetwork(final ParticleType<GustParticleData> type, final net.minecraft.network.FriendlyByteBuf buf) {
                return new GustParticleData(new Quaternionf(buf.readFloat(), buf.readFloat(), buf.readFloat(), buf.readFloat()));
            }
        };
    }

    @Override
    public void writeToNetwork(final net.minecraft.network.FriendlyByteBuf buffer) {
        buffer.writeFloat(this.orientation.x);
        buffer.writeFloat(this.orientation.y);
        buffer.writeFloat(this.orientation.z);
        buffer.writeFloat(this.orientation.w);
    }

    @Override
    public String writeToString() {
        return this.getType().toString();
    }


    @Override
    public ParticleType<?> getType() {
        return AeroParticleTypes.GUST.get();
    }
}
