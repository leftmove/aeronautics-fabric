package dev.eriksonn.aeronautics.fabric.service;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.service.AeroBlockStateService;
import net.minecraft.world.level.block.Block;

public class FabricAeroBlockStateService implements AeroBlockStateService {
	@Override
	public <T extends SmartPropellerBlock> void smartPropeller(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
	}
}
