package dev.simulated_team.simulated.fabric.events;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItemBindHandler;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.fabric.service.FabricSimpleResourceManagerRegistry;
import dev.simulated_team.simulated.index.SimKeys;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;

public final class SimFabricClientEvents {
	private SimFabricClientEvents() {
	}

	public static void register() {
		SimKeys.registerTo(KeyBindingHelper::registerKeyBinding);

		ClientTickEvents.START_CLIENT_TICK.register(SimulatedCommonClientEvents::preClientTick);
		ClientTickEvents.END_CLIENT_TICK.register(SimulatedCommonClientEvents::postClientTick);

		HudRenderCallback.EVENT.register((graphics, tickDelta) -> {
			SimulatedCommonClientEvents.renderOverlays(graphics, tickDelta.getGameTimeDeltaPartialTick(false));
			LinkedTypewriterItemBindHandler.OVERLAY.render(graphics, tickDelta);
		});

		ItemTooltipCallback.EVENT.register((stack, context, flag, lines) ->
				SimulatedCommonClientEvents.appendTooltip(stack, flag, Minecraft.getInstance().player, lines));

		int index = 0;
		for (final PreparableReloadListener listener : FabricSimpleResourceManagerRegistry.LISTENERS) {
			final ResourceLocation id = ResourceLocation.fromNamespaceAndPath("simulated", "client_reload_" + index++);
			ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(SimFabricCommonEvents.wrap(id, listener));
		}
	}
}
