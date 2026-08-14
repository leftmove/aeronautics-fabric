package dev.eriksonn.aeronautics.fabric.service;

import dev.eriksonn.aeronautics.fabric.content.fluids.levitite.LevititeBlendFluid;
import dev.eriksonn.aeronautics.service.AeroLevititeService;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public class FabricAeroLevititeService implements AeroLevititeService {
	@Override
	public Item getBucket() {
		return LevititeBlendFluid.BUCKET;
	}

	@Override
	public Fluid getFluid() {
		return LevititeBlendFluid.SOURCE;
	}
}
