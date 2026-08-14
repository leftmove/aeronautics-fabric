package dev.simulated_team.simulated.fabric.data;

import com.simibubi.create.AllTags;
import dev.simulated_team.simulated.compat.ItemComponents;
import dev.simulated_team.simulated.content.blocks.portable_engine.PortableEngineBlock;
import dev.simulated_team.simulated.fabric.index.SimFabricRecipeTypes;
import dev.simulated_team.simulated.index.SimBlocks;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public class PortableEngineDyeingRecipe extends CustomRecipe {

	public PortableEngineDyeingRecipe(final ResourceLocation id, final CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(final CraftingContainer input, final Level level) {
		int engines = 0;
		int dyes = 0;

		for (int i = 0; i < input.getContainerSize(); ++i) {
			final ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (Block.byItem(stack.getItem()) instanceof PortableEngineBlock) {
					++engines;
				} else {
					if (!stack.is(AllTags.forgeItemTag("dyes"))) {
						return false;
					}
					++dyes;
				}

				if (dyes > 1 || engines > 1) {
					return false;
				}
			}
		}

		return engines == 1 && dyes == 1;
	}

	@Override
	public ItemStack assemble(final CraftingContainer input, final RegistryAccess registries) {
		ItemStack engine = ItemStack.EMPTY;
		DyeColor color = DyeColor.RED;

		for (int i = 0; i < input.getContainerSize(); ++i) {
			final ItemStack stack = input.getItem(i);
			if (!stack.isEmpty()) {
				if (Block.byItem(stack.getItem()) instanceof PortableEngineBlock) {
					engine = stack;
				} else if (stack.getItem() instanceof net.minecraft.world.item.DyeItem dyeItem) {
					color = dyeItem.getDyeColor();
				}
			}
		}

		final ItemStack dyedEngine = SimBlocks.PORTABLE_ENGINES.get(color).asStack();
		if (!ItemComponents.patchOf(engine).isEmpty()) {
			ItemComponents.apply(dyedEngine, ItemComponents.patchOf(engine));
		}
		return dyedEngine;
	}

	@Override
	public boolean canCraftInDimensions(final int width, final int height) {
		return width * height >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return SimFabricRecipeTypes.PORTABLE_ENGINE_DYEING;
	}
}
