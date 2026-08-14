package dev.simulated_team.simulated.fabric.compat.jei;

import com.simibubi.create.AllTags;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.level.block.Block;

import java.util.Arrays;
import java.util.stream.Stream;

public final class PortableEngineDyeingRecipeMaker {

	public static Stream<CraftingRecipe> createRecipes() {
		final String group = "simulated.portable_engine.color";
		final ItemStack base = SimBlocks.PORTABLE_ENGINES.get(DyeColor.RED).asStack();
		final Ingredient baseIngredient = Ingredient.of(base);

		return Arrays.stream(DyeColor.values())
				.filter(dc -> dc != DyeColor.RED)
				.map(color -> {
					final TagKey<Item> colorTag = AllTags.forgeItemTag("dyes/" + color.getName());
					final Ingredient colorIngredient = Ingredient.of(colorTag);
					final NonNullList<Ingredient> inputs = NonNullList.of(Ingredient.EMPTY, baseIngredient, colorIngredient);
					final Block colored = SimBlocks.PORTABLE_ENGINES.get(color).get();
					final ItemStack output = new ItemStack(colored);
					final ResourceLocation id = Simulated.path(group + "/" + color);
					return new ShapelessRecipe(id, group, CraftingBookCategory.MISC, output, inputs);
				});
	}

	private PortableEngineDyeingRecipeMaker() {
	}
}
