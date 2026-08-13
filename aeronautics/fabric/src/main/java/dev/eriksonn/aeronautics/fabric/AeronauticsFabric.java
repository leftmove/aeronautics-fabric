package dev.eriksonn.aeronautics.fabric;

import com.simibubi.create.compat.jei.ConversionRecipe;
import com.simibubi.create.compat.jei.category.MysteriousItemConversionCategory;
import com.simibubi.create.content.processing.recipe.StandardProcessingRecipe;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.data.AeroAdvancementTriggers;
import dev.eriksonn.aeronautics.events.AeronauticsCommonEvents;
import dev.eriksonn.aeronautics.fabric.content.fluids.levitite.LevititeBlendFluid;
import dev.eriksonn.aeronautics.fabric.index.AeroParticleTypesFabric;
import dev.eriksonn.aeronautics.fabric.service.FabricAeroConfigService;
import dev.eriksonn.aeronautics.index.AeroAdvancements;
import dev.eriksonn.aeronautics.index.AeroArmInteractionPoints;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.AeroItems;
import dev.eriksonn.aeronautics.index.AeroTags;
import dev.simulated_team.simulated.registrate.RegistrateFlusher;
import dev.simulated_team.simulated.service.SimPlatformService;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.EmptyItemFluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.base.FullItemFluidStorage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;

public class AeronauticsFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		LevititeBlendFluid.register();
		AeroParticleTypesFabric.register();
		Aeronautics.init();
		RegistrateFlusher.flush(Aeronautics.getRegistrate());
		Aeronautics.getRegistrate().addExtraItem(Aeronautics.path("levitite_blend_bucket"));
		FabricAeroConfigService.register();

		registerFluidStorage();
		AeroArmInteractionPoints.init();
		AeroAdvancements.init();
		AeroAdvancementTriggers.register();

		ServerLifecycleEvents.SERVER_STOPPED.register(AeronauticsCommonEvents::onServerStopped);
		ServerTickEvents.END_WORLD_TICK.register(level -> {
			if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
				AeronauticsCommonEvents.onServerTickEnd(serverLevel);
			}
		});

		if (SimPlatformService.INSTANCE.isLoaded("jei")) {
			registerJeiConversions();
		}
	}

	private static void registerFluidStorage() {
		FluidStorage.combinedItemApiProvider(LevititeBlendFluid.BUCKET).register(context ->
				new FullItemFluidStorage(context, Items.BUCKET, FluidVariant.of(LevititeBlendFluid.SOURCE), FluidConstants.BUCKET));
		FluidStorage.combinedItemApiProvider(Items.BUCKET).register(context ->
				new EmptyItemFluidStorage(context, LevititeBlendFluid.BUCKET, LevititeBlendFluid.SOURCE, FluidConstants.BUCKET));
	}

	private static void registerJeiConversions() {
		MysteriousItemConversionCategory.RECIPES.add(
				ConversionRecipe.create(LevititeBlendFluid.BUCKET.getDefaultInstance(), AeroBlocks.LEVITITE.asItem().getDefaultInstance()));
		MysteriousItemConversionCategory.RECIPES.add(
				ConversionRecipe.create(LevititeBlendFluid.BUCKET.getDefaultInstance(), AeroBlocks.PEARLESCENT_LEVITITE.asItem().getDefaultInstance()));

		final ResourceLocation recipeId = Aeronautics.path("conversion_music_disc_cloud_skipper");
		final ConversionRecipe recipe = new StandardProcessingRecipe.Builder<>(ConversionRecipe::new, recipeId)
				.withItemIngredients(Ingredient.of(AeroTags.ItemTags.CONVERTS_TO_CLOUD_SKIPPER))
				.withSingleItemOutput(AeroItems.MUSIC_DISC_CLOUD_SKIPPER.asStack())
				.build();
		MysteriousItemConversionCategory.RECIPES.add(new RecipeHolder<>(recipeId, recipe));
	}
}
