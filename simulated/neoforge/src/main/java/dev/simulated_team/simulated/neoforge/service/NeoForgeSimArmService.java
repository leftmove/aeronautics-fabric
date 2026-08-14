package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlockEntity;
import dev.simulated_team.simulated.service.SimArmService;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class NeoForgeSimArmService implements SimArmService {
	@Override
	public ArmInteractionPoint createPortableEnginePoint(final ArmInteractionPointType type, final Level level, final BlockPos pos, final BlockState state) {
		return new PortableEngineInteractionPoint(type, level, pos, state);
	}

	public static class PortableEngineInteractionPoint extends AllArmInteractionPointTypes.DepotPoint {
		public PortableEngineInteractionPoint(final ArmInteractionPointType type, final Level level, final BlockPos pos, final BlockState state) {
			super(type, level, pos, state);
		}

		@Override
		public ItemStack insert(final ItemStack stack, final boolean simulate) {
			if (this.cachedState.hasBlockEntity()) {
				final BlockEntity be = this.level.getBlockEntity(this.pos);
				if (be instanceof final PortableEngineBlockEntity sbe) {
					return sbe.inventory.insertSlot(stack, 0, simulate);
				}
			}
			return super.insert(stack, simulate);
		}
	}
}
