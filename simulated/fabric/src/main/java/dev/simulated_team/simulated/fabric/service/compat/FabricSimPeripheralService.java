package dev.simulated_team.simulated.fabric.service.compat;

import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.simulated_team.simulated.service.compat.SimPeripheralService;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FabricSimPeripheralService implements SimPeripheralService {

	private static final List<Entry<BlockEntity, IPeripheral>> PERIPHERALS = new ArrayList<>();
	private static final List<Entry<BlockEntity, WiredElement>> WIRED_ELEMENTS = new ArrayList<>();

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> void addPeripheral(final Supplier<BlockEntityType<T>> typeSupplier, final CapabilityGetter<T, IPeripheral> getter) {
		PERIPHERALS.add((Entry<BlockEntity, IPeripheral>) new Entry<>(typeSupplier, getter));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends BlockEntity> void addWired(final Supplier<BlockEntityType<T>> typeSupplier, final CapabilityGetter<T, WiredElement> getter) {
		WIRED_ELEMENTS.add((Entry<BlockEntity, WiredElement>) new Entry<>(typeSupplier, getter));
	}

	public static void registerLookups() {
		if (!net.fabricmc.loader.api.FabricLoader.getInstance().isModLoaded("computercraft")) {
			return;
		}
		FabricCCCompat.register(PERIPHERALS, WIRED_ELEMENTS);
	}

	public record Entry<T extends BlockEntity, V>(Supplier<BlockEntityType<T>> typeSupplier, CapabilityGetter<T, V> getter) {
	}
}
