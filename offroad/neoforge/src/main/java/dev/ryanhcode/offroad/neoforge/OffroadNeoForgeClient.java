package dev.ryanhcode.offroad.neoforge;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.OffroadClient;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;

public final class OffroadNeoForgeClient {
	private OffroadNeoForgeClient() {
	}

	public static void init(final IEventBus modBus) {
		ModLoadingContext.get().registerExtensionPoint(
				ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory((minecraft, parent) -> new BaseConfigScreen(parent, Offroad.MOD_ID))
		);

		OffroadClient.init();
	}
}
