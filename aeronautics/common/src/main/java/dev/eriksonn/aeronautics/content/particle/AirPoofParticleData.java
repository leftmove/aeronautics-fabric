package dev.eriksonn.aeronautics.content.particle;

import com.mojang.serialization.MapCodec;
import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class AirPoofParticleData implements ParticleOptions, ICustomParticleDataWithSprite<AirPoofParticleData> {
    public static final AirPoofParticleData INSTANCE = new AirPoofParticleData();
    private static final MapCodec<AirPoofParticleData> CODEC = MapCodec.unit(INSTANCE);
    private static final StreamCodec<FriendlyByteBuf, AirPoofParticleData> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    private AirPoofParticleData() {}

    @Override
    public ParticleEngine.SpriteParticleRegistration<AirPoofParticleData> getMetaFactory() {
        return AirPoofParticle.Factory::new;
    }

    @Override
    public com.mojang.serialization.Codec<AirPoofParticleData> getCodec(final ParticleType<AirPoofParticleData> type) { return CODEC.codec(); }

    
    @Override
    public ParticleOptions.Deserializer<AirPoofParticleData> getDeserializer() {
        return new ParticleOptions.Deserializer<>() {
            @Override
            public AirPoofParticleData fromCommand(final ParticleType<AirPoofParticleData> type, final com.mojang.brigadier.StringReader reader) {
                return INSTANCE;
            }

            @Override
            public AirPoofParticleData fromNetwork(final ParticleType<AirPoofParticleData> type, final net.minecraft.network.FriendlyByteBuf buf) {
                return INSTANCE;
            }
        };
    }

    @Override
    public void writeToNetwork(final net.minecraft.network.FriendlyByteBuf buffer) {
    }

    @Override
    public String writeToString() {
        return this.getType().toString();
    }


    @Override
    public ParticleType<?> getType() {
        return AeroParticleTypes.AIR_POOF.get();
    }
}
