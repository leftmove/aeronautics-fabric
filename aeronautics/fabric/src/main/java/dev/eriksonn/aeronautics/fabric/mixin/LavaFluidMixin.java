package dev.eriksonn.aeronautics.fabric.mixin;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.LavaFluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFluid.class)
public class LavaFluidMixin {
	@Inject(method = "spreadTo", at = @At("HEAD"), cancellable = true)
	private void aeronautics$levititeInteraction(final LevelAccessor level, final BlockPos pos, final BlockState blockState, final Direction direction, final FluidState fluidState, final CallbackInfo ci) {
		if (direction != Direction.DOWN) {
			return;
		}
		final FluidState other = level.getFluidState(pos);
		if (other.getType() instanceof LevititeBlendDummyInterface) {
			level.setBlock(pos, other.isSource() ? Blocks.OBSIDIAN.defaultBlockState() : Blocks.CALCITE.defaultBlockState(), 3);
			level.levelEvent(1501, pos, 0);
			ci.cancel();
		}
	}
}
