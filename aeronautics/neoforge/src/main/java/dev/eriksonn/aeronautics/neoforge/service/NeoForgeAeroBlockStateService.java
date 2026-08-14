package dev.eriksonn.aeronautics.neoforge.service;

import com.simibubi.create.foundation.data.AssetLookup;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.service.AeroBlockStateService;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class NeoForgeAeroBlockStateService implements AeroBlockStateService {
	@Override
	public <T extends SmartPropellerBlock> void smartPropeller(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
		prov.getVariantBuilder(ctx.getEntry()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(AssetLookup.partialBaseModel(ctx, prov))
						.rotationY(state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0)
						.rotationX(state.getValue(SmartPropellerBlock.CEILING) ? 180 : 0)
						.build());
	}
}
