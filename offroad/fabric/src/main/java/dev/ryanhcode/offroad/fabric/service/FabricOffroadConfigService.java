package dev.ryanhcode.offroad.fabric.service;

import com.simibubi.create.api.stress.BlockStressValues;
import com.simibubi.create.infrastructure.config.CStress;
import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.config.OffroadConfig;
import dev.ryanhcode.offroad.config.client.OffroadClientConfig;
import dev.ryanhcode.offroad.config.server.OffroadServer;
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry;
import fuzs.forgeconfigapiport.api.config.v2.ModConfigEvents;
import net.createmod.catnip.config.ConfigBase;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public class FabricOffroadConfigService implements OffroadConfig {
	public static final Map<ModConfig.Type, ConfigBase> CONFIGS = new EnumMap<>(ModConfig.Type.class);

	private static OffroadServer server;
	private static OffroadClientConfig client;

	@Override
	public OffroadServer getServerConfig() {
		return server;
	}

	@Override
	public OffroadClientConfig getClientConfig() {
		return client;
	}

	private static <T extends ConfigBase> T register(final Supplier<T> factory, final ModConfig.Type side) {
		final Pair<T, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(builder -> {
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
		server = register(OffroadServer::new, ModConfig.Type.SERVER);
		client = register(OffroadClientConfig::new, ModConfig.Type.CLIENT);

		for (final Map.Entry<ModConfig.Type, ConfigBase> entry : CONFIGS.entrySet()) {
			ForgeConfigRegistry.INSTANCE.register(Offroad.MOD_ID, entry.getKey(), entry.getValue().specification);
		}

		ModConfigEvents.loading(Offroad.MOD_ID).register(config -> {
			for (final ConfigBase value : CONFIGS.values()) {
				if (value.specification == config.getSpec()) {
					value.onLoad();
				}
			}
		});
		ModConfigEvents.reloading(Offroad.MOD_ID).register(config -> {
			for (final ConfigBase value : CONFIGS.values()) {
				if (value.specification == config.getSpec()) {
					value.onReload();
				}
			}
		});

		final CStress stress = server.kinetics.stressValues;
		BlockStressValues.IMPACTS.registerProvider(stress::getImpact);
		BlockStressValues.CAPACITIES.registerProvider(stress::getCapacity);
	}
}
