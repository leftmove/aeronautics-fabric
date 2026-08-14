package dev.simulated_team.simulated.service;

import com.tterrag.registrate.providers.loot.RegistrateBlockLootTables;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootTable;

public interface SimLootService {
	SimLootService INSTANCE = ServiceUtil.load(SimLootService.class);

	void add(RegistrateBlockLootTables tables, Block block, LootTable.Builder table);
}
