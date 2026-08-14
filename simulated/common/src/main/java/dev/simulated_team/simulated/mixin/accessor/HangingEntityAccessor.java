package dev.simulated_team.simulated.mixin.accessor;

import net.minecraft.world.entity.decoration.HangingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(HangingEntity.class)
public interface HangingEntityAccessor {
    @Invoker("recalculateBoundingBox")
    void simulated$recalculateBoundingBox();
}
