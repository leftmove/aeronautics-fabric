package dev.eriksonn.aeronautics.data;

import com.simibubi.create.content.kinetics.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import dev.eriksonn.aeronautics.content.blocks.propeller.small.smart_propeller.SmartPropellerBlock;
import dev.eriksonn.aeronautics.service.AeroBlockStateService;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class AeroBlockStateGen {
	public static <T extends DirectionalAxisKineticBlock> void directionalPoweredAxisBlockstate(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
		BlockStateGen.directionalAxisBlock(ctx, prov, (blockState, vertical) -> prov.models()
				.getExistingFile(prov.modLoc("block/" + ctx.getName() + "/block_" + (vertical ? "vertical" : "horizontal") + (blockState.getValue(BlockStateProperties.POWERED) ? "_powered" : ""))));
	}

	public static <T extends SmartPropellerBlock> void smartPropellerBlockstate(final DataGenContext<Block, T> ctx, final RegistrateBlockstateProvider prov) {
		AeroBlockStateService.INSTANCE.smartPropeller(ctx, prov);
	}
}
