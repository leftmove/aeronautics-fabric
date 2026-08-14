package dev.simulated_team.simulated.service;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface SimArmService {
	SimArmService INSTANCE = ServiceUtil.load(SimArmService.class);

	ArmInteractionPoint createPortableEnginePoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state);
}
