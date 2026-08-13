package dev.simulated_team.simulated.fabric.service;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CStress;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.config.client.SimClient;
import dev.simulated_team.simulated.config.server.SimServer;
import dev.simulated_team.simulated.service.SimConfigService;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry;
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeModConfigEvents;
import net.createmod.catnip.config.ConfigBase;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class FabricSimConfigService implements SimConfigService {

	public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

	private static SimServer server;
	private static SimClient client;

	@Override
	public boolean serverLoaded() {
		return server != null && server.specification != null && server.specification.isLoaded();
	}

	@Override
	public boolean clientLoaded() {
		return client != null && client.specification != null && client.specification.isLoaded();
	}

	@Override
	public SimServer server() {
		return server;
	}

	@Override
	public SimClient client() {
		return client;
	}

	private static <T extends ConfigBase> T register(final Supplier<T> factory, final ModConfig.Type side) {
		final Pair<T, ModConfigSpec> specPair = new ModConfigSpec.Builder().configure(builder -> {
			final T config = factory.get();
			config.registerAll(builder);
			return config;
		});
		final T config = specPair.getLeft();
		config.specification = specPair.getRight();
		CONFIGS.put(side, config);
		return config;
	}

	public static void register() {
		server = register(SimServer::new, ModConfig.Type.SERVER);
		client = register(SimClient::new, ModConfig.Type.CLIENT);

		for (final Map.Entry<ModConfig.Type, ConfigBase> entry : CONFIGS.entrySet()) {
			NeoForgeConfigRegistry.INSTANCE.register(Simulated.MOD_ID, entry.getKey(), entry.getValue().specification);
		}

		NeoForgeModConfigEvents.loading(Simulated.MOD_ID).register(config -> {
			for (final ConfigBase value : CONFIGS.values()) {
				if (value.specification == config.getSpec()) {
					value.onLoad();
				}
			}
		});
		NeoForgeModConfigEvents.reloading(Simulated.MOD_ID).register(config -> {
			for (final ConfigBase value : CONFIGS.values()) {
				if (value.specification == config.getSpec()) {
					value.onReload();
				}
			}
		});

		final CStress stress = SimConfigService.INSTANCE.server().kinetics.stressValues;
		BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
		BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
	}
}
