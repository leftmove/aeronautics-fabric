package dev.simulated_team.simulated.fabric.multiloader.inventory;

import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ItemStorageWrapper implements Storage<ItemVariant> {

	private final AbstractContainer container;
	private final List<SlotStorage> slots;

	public ItemStorageWrapper(final AbstractContainer container) {
		this.container = container;
		this.slots = new ArrayList<>(container.getContainerSize());
		for (int i = 0; i < container.getContainerSize(); i++) {
			this.slots.add(new SlotStorage(i));
		}
	}

	@Override
	public long insert(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		long leftover = maxAmount;
		for (final SlotStorage slot : this.slots) {
			if (leftover <= 0) {
				break;
			}
			leftover -= slot.insert(resource, leftover, transaction);
		}
		return maxAmount - leftover;
	}

	@Override
	public long extract(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		long leftover = maxAmount;
		for (final SlotStorage slot : this.slots) {
			if (leftover <= 0) {
				break;
			}
			leftover -= slot.extract(resource, leftover, transaction);
		}
		return maxAmount - leftover;
	}

	@Override
	public @NotNull Iterator<StorageView<ItemVariant>> iterator() {
		return new ArrayList<StorageView<ItemVariant>>(this.slots).iterator();
	}

	private final class SlotStorage extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
		private final int slot;

		private SlotStorage(final int slot) {
			this.slot = slot;
		}

		@Override
		public long insert(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
			if (resource.isBlank() || maxAmount <= 0) {
				return 0;
			}
			this.updateSnapshots(transaction);
			final ItemStack toInsert = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
			final ItemStack leftover = ItemStorageWrapper.this.container.insertSlot(toInsert, this.slot, false);
			return toInsert.getCount() - leftover.getCount();
		}

		@Override
		public long extract(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
			if (resource.isBlank() || maxAmount <= 0) {
				return 0;
			}
			final ItemStack current = ItemStorageWrapper.this.container.getItem(this.slot);
			if (current.isEmpty() || !resource.matches(current)) {
				return 0;
			}
			this.updateSnapshots(transaction);
			final ItemStack extracted = ItemStorageWrapper.this.container.extractSlot(this.slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), false);
			return extracted.getCount();
		}

		@Override
		public boolean isResourceBlank() {
			return ItemStorageWrapper.this.container.getItem(this.slot).isEmpty();
		}

		@Override
		public ItemVariant getResource() {
			return ItemVariant.of(ItemStorageWrapper.this.container.getItem(this.slot));
		}

		@Override
		public long getAmount() {
			return ItemStorageWrapper.this.container.getItem(this.slot).getCount();
		}

		@Override
		public long getCapacity() {
			return ItemStorageWrapper.this.container.getMaxStackSize();
		}

		@Override
		protected ItemStack createSnapshot() {
			return ItemStorageWrapper.this.container.getItem(this.slot).copy();
		}

		@Override
		protected void readSnapshot(final ItemStack snapshot) {
			ItemStorageWrapper.this.container.setItem(this.slot, snapshot.copy());
		}
	}
}
