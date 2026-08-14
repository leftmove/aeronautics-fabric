package dev.simulated_team.simulated.fabric.service;

import com.simibubi.create.content.contraptions.MountedStorageManager;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import dev.simulated_team.simulated.fabric.multiloader.energy.SingleBatteryStorage;
import dev.simulated_team.simulated.fabric.multiloader.inventory.InventoryLoaderWrapperImpl;
import dev.simulated_team.simulated.fabric.multiloader.inventory.ItemStorageWrapper;
import dev.simulated_team.simulated.fabric.multiloader.tanks.SingleTankStorage;
import dev.simulated_team.simulated.multiloader.energy.SingleBattery;
import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import dev.simulated_team.simulated.service.SimInventoryService;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;
import team.reborn.energy.api.EnergyStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

public class FabricSimInventoryService implements SimInventoryService {

	public static final Set<InventoryGetterHolder<? extends BlockEntity>> INVENTORY_GETTERS = new HashSet<>();
	public static final Set<TankGetterHolder<? extends BlockEntity>> TANK_GETTERS = new HashSet<>();
	public static final Set<EnergyGetterHolder<? extends BlockEntity>> ENERGY_GETTERS = new HashSet<>();

	@Override
	public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerInventory(final BiFunction<T, Direction, AbstractContainer> getter) {
		return type -> INVENTORY_GETTERS.add(new InventoryGetterHolder<>(getter, type));
	}

	@Override
	public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerTank(final BiFunction<T, Direction, SingleTank> getter) {
		return type -> TANK_GETTERS.add(new TankGetterHolder<>(getter, type));
	}

	@Override
	public <T extends BlockEntity> NonNullConsumer<BlockEntityType<T>> registerBattery(final BiFunction<T, Direction, SingleBattery> getter) {
		return type -> ENERGY_GETTERS.add(new EnergyGetterHolder<>(getter, type));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends InventoryLoaderWrapper> T getInventory(@Nullable final BlockEntity be, @Nullable final Direction dir) {
		if (be == null || be.getLevel() == null) {
			return null;
		}

		final Storage<ItemVariant> storage = ItemStorage.SIDED.find(be.getLevel(), be.getBlockPos(), be.getBlockState(), be, dir);
		if (storage != null) {
			return (T) new InventoryLoaderWrapperImpl(storage);
		}
		return null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends InventoryLoaderWrapper> T getWrappedAllItemsFromContraption(final MountedStorageManager manager) {
		return (T) new InventoryLoaderWrapperImpl(manager.getAllItems());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends InventoryLoaderWrapper> T getWrappedMountedItemsFromContraption(final MountedStorageManager manager) {
		return (T) new InventoryLoaderWrapperImpl(manager.getMountedItems());
	}

	public static void registerLookups() {
		for (final InventoryGetterHolder<? extends BlockEntity> getter : INVENTORY_GETTERS) {
			ItemStorage.SIDED.registerForBlockEntity((be, dir) -> {
				final AbstractContainer container = getter.castBlockEntityAndGetInv(be, dir);
				return container == null ? null : new ItemStorageWrapper(container);
			}, getter.type());
		}

		for (final TankGetterHolder<? extends BlockEntity> getter : TANK_GETTERS) {
			FluidStorage.SIDED.registerForBlockEntity((be, dir) -> {
				final SingleTank tank = getter.castBlockEntityAndGetInv(be, dir);
				return tank == null ? null : new SingleTankStorage(tank);
			}, getter.type());
		}

		for (final EnergyGetterHolder<? extends BlockEntity> getter : ENERGY_GETTERS) {
			EnergyStorage.SIDED.registerForBlockEntity((be, dir) -> {
				final SingleBattery battery = getter.castBlockEntityAndGetInv(be, dir);
				return battery == null ? null : new SingleBatteryStorage(battery);
			}, getter.type());
		}
	}

	public record InventoryGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, AbstractContainer> getter, BlockEntityType<T> type) {
		@SuppressWarnings("unchecked")
		public AbstractContainer castBlockEntityAndGetInv(final BlockEntity be, final Direction dir) {
			return this.getter.apply((T) be, dir);
		}
	}

	public record TankGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, SingleTank> getter, BlockEntityType<T> type) {
		@SuppressWarnings("unchecked")
		public SingleTank castBlockEntityAndGetInv(final BlockEntity be, final Direction dir) {
			return this.getter.apply((T) be, dir);
		}
	}

	public record EnergyGetterHolder<T extends BlockEntity>(BiFunction<T, Direction, SingleBattery> getter, BlockEntityType<T> type) {
		@SuppressWarnings("unchecked")
		public SingleBattery castBlockEntityAndGetInv(final BlockEntity be, final Direction dir) {
			return this.getter.apply((T) be, dir);
		}
	}
}
