package dev.eriksonn.aeronautics.neoforge.mixin.self_mixins;

import dev.eriksonn.aeronautics.content.blocks.hot_air.gust.GustEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(GustEntity.class)
public abstract class GustEntityMixin implements IEntityAdditionalSpawnData {
	@Shadow
	public abstract void writeSpawnData(RegistryFriendlyByteBuf buffer);

	@Shadow
	public abstract void readSpawnData(RegistryFriendlyByteBuf additionalData);

	@Override
	public void writeSpawnData(final FriendlyByteBuf buffer) {
		this.writeSpawnData(new RegistryFriendlyByteBuf(buffer));
	}

	@Override
	public void readSpawnData(final FriendlyByteBuf additionalData) {
		this.readSpawnData(new RegistryFriendlyByteBuf(additionalData));
	}
}
