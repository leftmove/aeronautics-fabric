package dev.simulated_team.simulated.fabric.mixin.self_mixins;

import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import dev.simulated_team.simulated.entity.ExtraSpawnData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HoneyGlueEntity.class)
public abstract class HoneyGlueEntityMixin implements ExtraSpawnData {
	@Shadow
	public abstract void addAdditionalSaveData(CompoundTag tag);

	@Shadow
	public abstract void readAdditionalSaveData(CompoundTag tag);

	@Override
	public void writeSpawnData(final RegistryFriendlyByteBuf buf) {
		final CompoundTag compound = new CompoundTag();
		this.addAdditionalSaveData(compound);
		buf.writeNbt(compound);
	}

	@Override
	public void readSpawnData(final RegistryFriendlyByteBuf buf) {
		this.readAdditionalSaveData(buf.readNbt());
	}
}
