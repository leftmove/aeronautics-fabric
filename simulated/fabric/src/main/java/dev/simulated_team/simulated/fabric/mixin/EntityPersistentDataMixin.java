package dev.simulated_team.simulated.fabric.mixin;

import dev.simulated_team.simulated.fabric.entity.PersistentDataHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityPersistentDataMixin implements PersistentDataHolder {
	@Unique
	private CompoundTag simulated$persistentData;

	@Override
	public CompoundTag simulated$getPersistentData() {
		if (this.simulated$persistentData == null) {
			this.simulated$persistentData = new CompoundTag();
		}
		return this.simulated$persistentData;
	}

	@Inject(method = "saveWithoutId", at = @At("RETURN"))
	private void simulated$savePersistentData(final CompoundTag tag, final CallbackInfoReturnable<CompoundTag> cir) {
		if (this.simulated$persistentData != null && !this.simulated$persistentData.isEmpty()) {
			tag.put("ForgeData", this.simulated$persistentData);
		}
	}

	@Inject(method = "load", at = @At("RETURN"))
	private void simulated$loadPersistentData(final CompoundTag tag, final CallbackInfo ci) {
		if (tag.contains("ForgeData", CompoundTag.TAG_COMPOUND)) {
			this.simulated$persistentData = tag.getCompound("ForgeData");
		}
	}
}
