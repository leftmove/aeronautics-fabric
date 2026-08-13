package dev.simulated_team.simulated.fabric;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.fabric.entity.ExtraSpawnDataHandler;
import dev.simulated_team.simulated.fabric.events.SimFabricCommonEvents;
import dev.simulated_team.simulated.fabric.index.FabricSimStats;
import dev.simulated_team.simulated.fabric.index.SimFabricRecipeTypes;
import dev.simulated_team.simulated.fabric.index.SimParticleTypesFabric;
import dev.simulated_team.simulated.fabric.service.FabricSimConfigService;
import dev.simulated_team.simulated.index.SimBlocks;
import dev.simulated_team.simulated.registrate.RegistrateFlusher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

public final class SimulatedFabric implements ModInitializer {
	public static final CreativeModeTab TAB = FabricItemGroup.builder()
			.title(Component.translatable("itemGroup." + Simulated.MOD_ID + ".group"))
			.icon(() -> new ItemStack(SimBlocks.PHYSICS_ASSEMBLER.get()))
			.build();

	@Override
	public void onInitialize() {
		Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, Simulated.path("main_tab"), TAB);

		FabricSimStats.register();
		Simulated.init();
		RegistrateFlusher.flush(Simulated.getRegistrate());
		SimParticleTypesFabric.register();
		SimFabricRecipeTypes.register();
		FabricSimConfigService.register();
		ExtraSpawnDataHandler.register();
		SimFabricCommonEvents.register();
	}
}
