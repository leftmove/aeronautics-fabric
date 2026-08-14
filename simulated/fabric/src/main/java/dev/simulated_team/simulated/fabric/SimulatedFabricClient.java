package dev.simulated_team.simulated.fabric;

import com.simibubi.create.foundation.item.render.CustomRenderedItemModelRenderer;
import com.simibubi.create.foundation.item.render.CustomRenderedItems;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.SimulatedClient;
import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItemRenderer;
import dev.simulated_team.simulated.content.physics_staff.PhysicsStaffItemRenderer;
import dev.simulated_team.simulated.fabric.entity.ExtraSpawnDataHandler;
import dev.simulated_team.simulated.fabric.events.SimFabricClientEvents;
import dev.simulated_team.simulated.fabric.index.SimParticleTypesFabric;
import dev.simulated_team.simulated.index.SimItems;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;

public class SimulatedFabricClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		SimFabricClientEvents.register();
		SimParticleTypesFabric.registerClient();
		ExtraSpawnDataHandler.registerClient();
		FabricVisualizerQueue.applyAll();
		registerItemRenderers();
		SimulatedClient.init();
	}

	private static void registerItemRenderers() {
		registerRenderer(SimItems.PHYSICS_STAFF.get(), new PhysicsStaffItemRenderer());
		registerRenderer(SimItems.PLUNGER_LAUNCHER.get(), new PlungerLauncherItemRenderer());
	}

	private static void registerRenderer(final net.minecraft.world.item.Item item, final CustomRenderedItemModelRenderer renderer) {
		BuiltinItemRendererRegistry.INSTANCE.register(item, renderer);
		CustomRenderedItems.register(item);
	}

	public static BaseConfigScreen configScreen(final net.minecraft.client.gui.screens.Screen parent) {
		return new BaseConfigScreen(parent, Simulated.MOD_ID);
	}
}
