package dev.simulated_team.simulated.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {
	@Inject(method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z", at = @At("HEAD"), cancellable = true)
	private static void simulated$connectRedstone(final BlockState state, final Direction direction, final CallbackInfoReturnable<Boolean> cir) {
		if (state.getBlock() instanceof final CommonRedstoneBlock redstone) {
			cir.setReturnValue(redstone.commonConnectRedstone(state, null, BlockPos.ZERO, direction));
		}
	}
}
