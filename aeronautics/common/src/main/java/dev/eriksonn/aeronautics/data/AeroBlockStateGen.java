package dev.eriksonn.aeronautics.data;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.data.AssetLookup;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;

public class AeroBlockStateGen {
	public static <T extends DirectionalAxisKineticBlock> void directionalPoweredAxisBlockstate(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
		BlockStateGen.directionalAxisBlock(ctx, prov, (blockState, vertical) -> prov.models()
				.getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical ? "vertical" : "horizontal") + (blockState.getValue(BlockStateProperties.POWERED) ? "_powered" : ""))));
	}

	public static <T extends SmartPropellerBlock> void smartPropellerBlockstate(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
		prov.getVariantBuilder(ctx.getEntry()).forAllStates(state ->
				ConfiguredModel.builder().modelFile(AssetLookup.partialBaseModel(ctx, prov))
						.rotationY(state.getValue(BlockStateProperties.HORIZONTAL_AXIS) == Direction.Axis.X ? 90 : 0)
						.rotationX(state.getValue(SmartPropellerBlock.CEILING) ? 180 : 0)
						.build());
	}
}
