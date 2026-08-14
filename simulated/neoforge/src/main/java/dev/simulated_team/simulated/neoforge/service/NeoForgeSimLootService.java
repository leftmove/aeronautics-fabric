package dev.simulated_team.simulated.neoforge.service;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import dev.simulated_team.simulated.service.SimLootService;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public class NeoForgeSimLootService implements SimLootService {
	@Override
	public void add(final RegistrateBlockLootTables tables, final Block block, final LootTable.Builder table) {
		tables.m_247577_(block, table);
	}
}
