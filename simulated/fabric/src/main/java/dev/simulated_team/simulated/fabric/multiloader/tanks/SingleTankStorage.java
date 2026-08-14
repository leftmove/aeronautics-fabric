package dev.simulated_team.simulated.fabric.multiloader.tanks;

import dev.simulated_team.simulated.multiloader.tanks.CFluidType;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.util.Tuple;
import dev.simulated_team.simulated.compat.ItemComponents;

public class SingleTankStorage extends SnapshotParticipant<Tuple<CFluidType, Long>> implements SingleSlotStorage<FluidVariant> {

	private final SingleTank tank;

	public SingleTankStorage(final SingleTank tank) {
		this.tank = tank;
	}

	public static FluidVariant toVariant(final CFluidType type) {
		if (type == null || type.isBlank()) {
			return FluidVariant.blank();
		}
		final DataComponentPatch patch = type.data() == null ? DataComponentPatch.EMPTY : type.data();
		return FluidVariant.of(type.fluid(), patch);
	}

	public static CFluidType toCType(final FluidVariant variant) {
		if (variant.isBlank()) {
			return CFluidType.BLANK;
		}
		return new CFluidType(variant.getFluid(), ItemComponents.view(variant));
	}

	@Override
	public long insert(final FluidVariant insertedVariant, final long maxAmount, final TransactionContext transaction) {
		if (insertedVariant.isBlank() || maxAmount <= 0) {
			return 0;
		}
		this.updateSnapshots(transaction);
		return this.tank.insert(toCType(insertedVariant), maxAmount, false, null);
	}

	@Override
	public long extract(final FluidVariant extractedVariant, final long maxAmount, final TransactionContext transaction) {
		if (extractedVariant.isBlank() || maxAmount <= 0) {
			return 0;
		}
		this.updateSnapshots(transaction);
		return this.tank.extract(toCType(extractedVariant), maxAmount, false, null);
	}

	@Override
	public boolean isResourceBlank() {
		return this.tank.type.isBlank() || this.tank.amount <= 0;
	}

	@Override
	public FluidVariant getResource() {
		return toVariant(this.tank.type);
	}

	@Override
	public long getAmount() {
		return this.tank.amount;
	}

	@Override
	public long getCapacity() {
		return this.tank.capacity;
	}

	@Override
	protected Tuple<CFluidType, Long> createSnapshot() {
		return this.tank.createSnapshot();
	}

	@Override
	protected void readSnapshot(final Tuple<CFluidType, Long> snapshot) {
		this.tank.readSnapshot(snapshot.getA(), snapshot.getB());
	}
}
