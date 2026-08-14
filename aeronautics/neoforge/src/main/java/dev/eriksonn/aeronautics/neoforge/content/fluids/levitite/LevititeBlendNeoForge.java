package dev.eriksonn.aeronautics.neoforge.content.fluids.levitite;

import dev.eriksonn.aeronautics.api.levitite_blend_crystallization.LevititeBlendDummyInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class LevititeBlendNeoForge extends ForgeFlowingFluid.Source implements LevititeBlendDummyInterface {
	public LevititeBlendNeoForge(final Properties properties) {
		super(properties);
	}

	@Override
	public void tick(final Level level, final BlockPos pos, final FluidState state) {
		super.tick(level, pos, state);
		LevititeBlendDummyInterface.super.levititeBlendTick(level, pos, state);
	}
}
