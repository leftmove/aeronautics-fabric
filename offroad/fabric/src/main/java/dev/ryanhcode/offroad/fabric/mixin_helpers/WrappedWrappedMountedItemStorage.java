package dev.ryanhcode.offroad.fabric.mixin_helpers;

import com.simibubi.create.content.contraptions.Contraption;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.contraptions.borehead_contraption.BoreheadBearingContraption;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlottedStackStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.world.item.ItemStack;

import java.lang.ref.WeakReference;
import java.util.Iterator;

public class WrappedWrappedMountedItemStorage implements SlottedStackStorage {
	private final WeakReference<Contraption> associatedContraption;
	private final SlottedStackStorage wrappedInv;

	public WrappedWrappedMountedItemStorage(final WeakReference<Contraption> associatedContraption, final SlottedStackStorage wrappedInv) {
		this.associatedContraption = associatedContraption;
		this.wrappedInv = wrappedInv;
	}

	@Override
	public void setStackInSlot(final int i, final ItemStack itemStack) {
		final Contraption contraption = this.associatedContraption.get();
		if (contraption instanceof final BoreheadBearingContraption bce && itemStack.isEmpty()) {
			((BoreheadAttachedStorage) bce.getStorage()).invokeUnstall();
		}
		this.wrappedInv.setStackInSlot(i, itemStack);
	}

	@Override
	public int getSlotCount() {
		return this.wrappedInv.getSlotCount();
	}

	@Override
	public ItemStack getStackInSlot(final int i) {
		return this.wrappedInv.getStackInSlot(i);
	}

	@Override
	public int getSlotLimit(final int slot) {
		return this.wrappedInv.getSlotLimit(slot);
	}

	@Override
	public net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage<ItemVariant> getSlot(final int slot) {
		return this.wrappedInv.getSlot(slot);
	}

	@Override
	public long insertSlot(final int slot, final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		return this.wrappedInv.insertSlot(slot, resource, maxAmount, transaction);
	}

	@Override
	public long extractSlot(final int slot, final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		final long extracted = this.wrappedInv.extractSlot(slot, resource, maxAmount, transaction);
		final Contraption contraption = this.associatedContraption.get();
		if (contraption instanceof final BoreheadBearingContraption bce && extracted > 0) {
			((BoreheadAttachedStorage) bce.getStorage()).invokeUnstall();
		}
		return extracted;
	}

	@Override
	public long insert(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		return this.wrappedInv.insert(resource, maxAmount, transaction);
	}

	@Override
	public long extract(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
		final long extracted = this.wrappedInv.extract(resource, maxAmount, transaction);
		final Contraption contraption = this.associatedContraption.get();
		if (contraption instanceof final BoreheadBearingContraption bce && extracted > 0) {
			((BoreheadAttachedStorage) bce.getStorage()).invokeUnstall();
		}
		return extracted;
	}

	@Override
	public Iterator<StorageView<ItemVariant>> iterator() {
		return this.wrappedInv.iterator();
	}
}
