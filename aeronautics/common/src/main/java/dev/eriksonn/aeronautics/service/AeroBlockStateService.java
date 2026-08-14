package dev.eriksonn.aeronautics.service;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.simulated_team.simulated.service.ServiceUtil;
import net.minecraft.world.level.block.Block;

public interface AeroBlockStateService {
	AeroBlockStateService INSTANCE = ServiceUtil.load(AeroBlockStateService.class);

	<T extends SmartPropellerBlock> void smartPropeller(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov);
}
