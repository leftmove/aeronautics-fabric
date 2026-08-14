package dev.simulated_team.simulated.fabric.service;

import dev.simulated_team.simulated.service.SimEntityDataSerialization;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class FabricSimEntityDataSerialization implements SimEntityDataSerialization {

	@Override
	public <A, T extends EntityDataSerializer<A>> void registerDataSerializer(final String name, final T serializer) {
		EntityDataSerializers.registerSerializer(serializer);
	}
}
