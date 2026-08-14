package dev.eriksonn.aeronautics.service;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface AeroArmService {
	AeroArmService INSTANCE = ServiceUtil.load(AeroArmService.class);

	ArmInteractionPoint createMountedPotatoCannonPoint(ArmInteractionPointType type, Level level, BlockPos pos, BlockState state);
}
