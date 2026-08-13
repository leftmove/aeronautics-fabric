package dev.simulated_team.simulated.fabric.index;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.index.SimStats;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.Stats;

public class FabricSimStats extends SimStats {

	public static void register() {
		new FabricSimStats().init();
	}

	@Override
	protected SimStats.Stat makeCustomStat(final String key, final StatFormatter formatter) {
		final ResourceLocation id = Simulated.path(key);
		Registry.register(BuiltInRegistries.CUSTOM_STAT, id, id);
		Stats.CUSTOM.get(id, formatter);
		return new SimStats.Stat(() -> id, formatter);
	}
}
