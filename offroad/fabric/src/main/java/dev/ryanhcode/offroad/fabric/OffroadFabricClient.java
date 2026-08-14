package dev.ryanhcode.offroad.fabric;

import dev.ryanhcode.offroad.OffroadClient;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class OffroadFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		OffroadClient.init();
		ClientTickEvents.END_WORLD_TICK.register(OffroadCommonEvents::tickLevelEvent);
	}
}
