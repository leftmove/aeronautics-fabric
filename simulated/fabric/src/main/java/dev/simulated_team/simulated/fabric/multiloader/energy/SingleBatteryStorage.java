package dev.simulated_team.simulated.fabric.multiloader.energy;

import dev.simulated_team.simulated.multiloader.energy.SingleBattery;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import team.reborn.energy.api.EnergyStorage;

public class SingleBatteryStorage extends SnapshotParticipant<Integer> implements EnergyStorage {

	private final SingleBattery battery;

	public SingleBatteryStorage(final SingleBattery battery) {
		this.battery = battery;
	}

	@Override
	public long insert(final long maxAmount, final TransactionContext transaction) {
		if (maxAmount <= 0 || !this.battery.canReceive()) {
			return 0;
		}
		this.updateSnapshots(transaction);
		return this.battery.receiveEnergy((int) Math.min(maxAmount, Integer.MAX_VALUE), false);
	}

	@Override
	public long extract(final long maxAmount, final TransactionContext transaction) {
		if (maxAmount <= 0 || !this.battery.canExtract()) {
			return 0;
		}
		this.updateSnapshots(transaction);
		return this.battery.extractEnergy((int) Math.min(maxAmount, Integer.MAX_VALUE), false);
	}

	@Override
	public long getAmount() {
		return this.battery.getEnergy();
	}

	@Override
	public long getCapacity() {
		return this.battery.maxEnergy;
	}

	@Override
	protected Integer createSnapshot() {
		return this.battery.getEnergy();
	}

	@Override
	protected void readSnapshot(final Integer snapshot) {
		this.battery.setEnergy(snapshot);
	}
}
