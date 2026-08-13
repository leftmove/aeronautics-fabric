package dev.eriksonn.aeronautics.fabric.index;

import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.index.AeroParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class AeroParticleTypesFabric {
	private AeroParticleTypesFabric() {
	}

	public static void register() {
		for (final AeroParticleTypes type : AeroParticleTypes.values()) {
			Registry.register(BuiltInRegistries.PARTICLE_TYPE, Aeronautics.path(CreateLang.asId(type.name())), type.get());
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerClient() {
		AeroParticleTypes.registerClientParticles(entry -> {
			final ParticleType particleType = entry.getObject();
			final Object factory = entry.getTypeFactory().get();
			if (factory instanceof final ICustomParticleDataWithSprite spriteData) {
				ParticleFactoryRegistry.getInstance().register(particleType, spriteData.getMetaFactory()::create);
			}
		});
	}
}
