package dev.ryanhcode.offroad.fabric;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import dev.ryanhcode.offroad.fabric.service.FabricOffroadConfigService;
import dev.simulated_team.simulated.registrate.RegistrateFlusher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class OffroadFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Offroad.init();
		RegistrateFlusher.flush(Offroad.getRegistrate());
		FabricOffroadConfigService.register();
		OffroadCommonEvents.modifyDefaultComponents();

		ServerTickEvents.END_WORLD_TICK.register(OffroadCommonEvents::tickLevelEvent);
	}
}
