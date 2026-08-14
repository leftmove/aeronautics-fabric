package dev.simulated_team.simulated.fabric.service;

import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import dev.simulated_team.simulated.service.SimItemService;
import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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

	@Override
	public List<ItemStack> getLinkedControllerFrequencyItems(final ItemStack stack) {
		final var handler = LinkedControllerItem.getFrequencyItems(stack);
		final List<ItemStack> items = new ArrayList<>();
		final int slots = handler.getSlotCount();
		for (int i = 0; i < Math.max(12, slots); i++) {
			items.add(i < slots ? handler.getStackInSlot(i) : ItemStack.EMPTY);
		}
		return items;
	}
}
