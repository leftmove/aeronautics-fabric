package dev.simulated_team.simulated.fabric.service;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import dev.simulated_team.simulated.service.SimLootService;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public class FabricSimLootService implements SimLootService {
	@Override
	public void add(final RegistrateBlockLootTables tables, final Block block, final LootTable.Builder table) {
		tables.add(block, table);
	}
}
