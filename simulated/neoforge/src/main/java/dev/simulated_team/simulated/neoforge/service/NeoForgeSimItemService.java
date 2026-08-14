package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.AllTags;
import com.simibubi.create.content.redstone.link.controller.LinkedControllerItem;
import dev.simulated_team.simulated.service.SimItemService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeSimItemService implements SimItemService {

    public int getBurnTime(final ItemStack stack) {
        return stack.getBurnTime(RecipeType.SMELTING);
    }

    @Override
    public int getSuperheatedBurnTime(final ItemStack stack) {
        if (AllTags.AllItemTags.BLAZE_BURNER_FUEL_SPECIAL.matches(stack)) {
            final int burnTime = stack.getBurnTime(RecipeType.SMELTING);
            return burnTime > 0 ? burnTime : 3200;
        }
        return 0;
    }

    @Override
    public List<ItemStack> getLinkedControllerFrequencyItems(final ItemStack stack) {
        final var handler = LinkedControllerItem.getFrequencyItems(stack);
        final List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < Math.max(12, handler.getSlots()); i++) {
            items.add(i < handler.getSlots() ? handler.getStackInSlot(i) : ItemStack.EMPTY);
        }
        return items;
    }
}
