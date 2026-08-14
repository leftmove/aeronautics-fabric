package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.AllTags;
import dev.simulated_team.simulated.service.SimItemService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;

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
}
