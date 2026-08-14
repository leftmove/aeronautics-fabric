package dev.simulated_team.simulated.fabric.service;

import dev.simulated_team.simulated.service.SimPlatformService;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

public class FabricSimPlatformService implements SimPlatformService {

	private static MinecraftServer server;

	public static void setServer(final MinecraftServer current) {
		server = current;
	}

	@Override
	public boolean isLoaded(final String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public MinecraftServer getCurrentServer() {
		return server;
	}
}
