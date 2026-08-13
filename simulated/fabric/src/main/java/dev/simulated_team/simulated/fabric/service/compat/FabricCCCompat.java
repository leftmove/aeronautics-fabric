package dev.simulated_team.simulated.fabric.service.compat;

import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.network.wired.WiredElementLookup;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.PeripheralLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public final class FabricCCCompat {
	private FabricCCCompat() {
	}

	public static void register(
			final List<FabricSimPeripheralService.Entry<BlockEntity, IPeripheral>> peripherals,
			final List<FabricSimPeripheralService.Entry<BlockEntity, WiredElement>> wiredElements
	) {
		if (!FabricLoader.getInstance().isModLoaded("computercraft")) {
			return;
		}

		for (final FabricSimPeripheralService.Entry<BlockEntity, IPeripheral> entry : peripherals) {
			PeripheralLookup.get().registerForBlockEntity((be, dir) -> entry.getter().get(be, dir), entry.typeSupplier().get());
		}

		for (final FabricSimPeripheralService.Entry<BlockEntity, WiredElement> entry : wiredElements) {
			WiredElementLookup.get().registerForBlockEntity((be, dir) -> entry.getter().get(be, dir), entry.typeSupplier().get());
		}
	}
}
