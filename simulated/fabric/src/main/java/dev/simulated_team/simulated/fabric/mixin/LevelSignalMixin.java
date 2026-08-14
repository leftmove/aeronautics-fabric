package dev.simulated_team.simulated.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.simulated_team.simulated.multiloader.CommonRedstoneBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SignalGetter.class)
public interface LevelSignalMixin {
	@WrapOperation(method = "getSignal", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;isRedstoneConductor(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z"))
	default boolean simulated$checkWeakPower(final BlockState state, final BlockGetter level, final BlockPos pos, final Operation<Boolean> original, final BlockPos signalPos, final Direction direction) {
		if (state.getBlock() instanceof final CommonRedstoneBlock redstone) {
			return redstone.commonCheckWeakPower(state, (SignalGetter) this, pos, direction);
		}
		return original.call(state, level, pos);
	}
}
