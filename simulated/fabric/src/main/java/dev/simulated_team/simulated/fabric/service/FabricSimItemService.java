package dev.simulated_team.simulated.fabric.service;

import dev.simulated_team.simulated.service.SimItemService;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

public class FabricSimItemService implements SimItemService {

	@Override
	public int getBurnTime(final ItemStack stack) {
		final Integer time = FuelRegistry.INSTANCE.get(stack.getItem());
		return time == null ? 0 : time;
	}

	@Override
	public int getSuperheatedBurnTime(final ItemStack stack) {
		return 0;
	}
}
