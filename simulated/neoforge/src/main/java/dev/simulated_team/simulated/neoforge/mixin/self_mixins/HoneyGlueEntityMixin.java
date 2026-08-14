package dev.simulated_team.simulated.neoforge.mixin.self_mixins;

import dev.simulated_team.simulated.content.entities.honey_glue.HoneyGlueEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(HoneyGlueEntity.class)
public abstract class HoneyGlueEntityMixin implements IEntityAdditionalSpawnData {

    @Shadow
    public abstract void addAdditionalSaveData(CompoundTag tag);

    @Shadow
    public abstract void readAdditionalSaveData(CompoundTag tag);

    @Override
    public void writeSpawnData(final FriendlyByteBuf buf) {
        final CompoundTag compound = new CompoundTag();
        this.addAdditionalSaveData(compound);
        buf.writeNbt(compound);
    }

    @Override
    public void readSpawnData(final FriendlyByteBuf buf) {
        this.readAdditionalSaveData(buf.readNbt());
    }
}
