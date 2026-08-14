package dev.eriksonn.aeronautics.fabric.service;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.eriksonn.aeronautics.content.blocks.mounted_potato_cannon.MountedPotatoCannonBlockEntity;
import dev.eriksonn.aeronautics.service.AeroArmService;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class FabricAeroArmService implements AeroArmService {
	@Override
	public ArmInteractionPoint createMountedPotatoCannonPoint(final ArmInteractionPointType type, final Level level, final BlockPos pos, final BlockState state) {
		return new MountedPotatoCannonPoint(type, level, pos, state);
	}

	public static class MountedPotatoCannonPoint extends AllArmInteractionPointTypes.DepositOnlyArmInteractionPoint {
		public MountedPotatoCannonPoint(final ArmInteractionPointType type, final Level level, final BlockPos pos, final BlockState state) {
			super(type, level, pos, state);
		}

		@Override
		public ItemStack insert(final ItemStack stack, final TransactionContext ctx) {
			if (this.cachedState.hasBlockEntity()) {
				final BlockEntity be = this.level.getBlockEntity(this.pos);
				if (be instanceof final MountedPotatoCannonBlockEntity sbe) {
					final ItemStack leftover = sbe.getInventory().insertSlot(stack.copy(), 0, true);
					if (leftover.getCount() != stack.getCount()) {
						TransactionCallback.onSuccess(ctx, () -> sbe.getInventory().insertSlot(stack.copy(), 0, false));
					}
					return leftover;
				}
			}
			return super.insert(stack, ctx);
		}
	}
}
