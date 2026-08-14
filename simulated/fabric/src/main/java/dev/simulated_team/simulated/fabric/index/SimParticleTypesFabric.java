package dev.simulated_team.simulated.fabric.index;

import com.simibubi.create.foundation.particle.ICustomParticleDataWithSprite;
import com.simibubi.create.foundation.utility.CreateLang;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimParticleTypes;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;

public final class SimParticleTypesFabric {
	private SimParticleTypesFabric() {
	}

	public static void register() {
		for (final SimParticleTypes type : SimParticleTypes.values()) {
			final String name = CreateLang.asId(type.name());
			Registry.register(BuiltInRegistries.PARTICLE_TYPE, Simulated.path(name), type.get());
		}
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerClient() {
		SimParticleTypes.registerClientParticles(entry -> {
			final ParticleType particleType = entry.getObject();
			final Object factory = entry.getTypeFactory().get();
			if (factory instanceof final ICustomParticleDataWithSprite spriteData) {
				ParticleFactoryRegistry.getInstance().register(particleType, spriteData.getMetaFactory()::create);
			}
		});
	}
}
