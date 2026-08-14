package dev.simulated_team.simulated.entity;

import net.minecraft.network.RegistryFriendlyByteBuf;

public interface ExtraSpawnData {
	void writeSpawnData(RegistryFriendlyByteBuf buf);

	void readSpawnData(RegistryFriendlyByteBuf buf);
}
