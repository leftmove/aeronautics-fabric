package dev.simulated_team.simulated.fabric.multiloader.inventory;

import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.ItemHelper.ExtractionCountMode;
import dev.simulated_team.simulated.multiloader.inventory.InventoryLoaderWrapper;
import dev.simulated_team.simulated.multiloader.inventory.ItemInfoWrapper;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

public class InventoryLoaderWrapperImpl extends InventoryLoaderWrapper {

	private final Storage<ItemVariant> transferStorage;
	private final IItemHandler itemHandler;

	public InventoryLoaderWrapperImpl(final Storage<ItemVariant> transferStorage) {
		this.transferStorage = transferStorage;
		this.itemHandler = null;
	}

	public InventoryLoaderWrapperImpl(final IItemHandler itemHandler) {
		this.transferStorage = null;
		this.itemHandler = itemHandler;
	}

	@Override
	public ItemStack extractAny(final int maxAmount, final boolean simulate, final boolean exact) {
		if (this.itemHandler != null) {
			final ItemStack extracted = ItemHelper.extract(this.itemHandler, $ -> true, exact ? ExtractionCountMode.EXACTLY : ExtractionCountMode.UPTO, maxAmount, simulate);
			if (this.callback != null && !extracted.isEmpty() && !simulate) {
				this.callback.accept(true);
			}
			return extracted;
		}

		try (final Transaction transaction = Transaction.openOuter()) {
			for (final StorageView<ItemVariant> view : this.transferStorage) {
				if (view.isResourceBlank() || view.getAmount() <= 0) {
					continue;
				}
				final ItemVariant variant = view.getResource();
				final long extracted = view.extract(variant, maxAmount, transaction);
				if (extracted <= 0) {
					continue;
				}
				if (exact && extracted != maxAmount) {
					return ItemStack.EMPTY;
				}
				if (!simulate) {
					transaction.commit();
					if (this.callback != null) {
						this.callback.accept(true);
					}
				}
				return variant.toStack((int) extracted);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int insertGeneral(final ItemInfoWrapper info, final int amountToInsert, final boolean simulate) {
		final ItemStack is = ItemInfoWrapper.generateFromInfo(info);
		is.setCount(amountToInsert);

		if (this.itemHandler != null) {
			final int amountInserted = amountToInsert - ItemHandlerHelper.insertItem(this.itemHandler, is, simulate).getCount();
			if (this.callback != null && amountInserted > 0 && !simulate) {
				this.callback.accept(false);
			}
			return amountInserted;
		}

		try (final Transaction transaction = Transaction.openOuter()) {
			final long inserted = this.transferStorage.insert(ItemVariant.of(is), amountToInsert, transaction);
			if (!simulate) {
				transaction.commit();
				if (this.callback != null && inserted > 0) {
					this.callback.accept(false);
				}
			}
			return (int) inserted;
		}
	}

	@Override
	public ItemStack insertSlot(final ItemStack stack, final int slot, final boolean simulate) {
		if (this.itemHandler != null) {
			final ItemStack leftover = this.itemHandler.insertItem(slot, stack, simulate);
			if (this.callback != null && !stack.equals(leftover) && !simulate) {
				this.callback.accept(false);
			}
			return leftover;
		}

		try (final Transaction transaction = Transaction.openOuter()) {
			final long inserted = this.transferStorage.insert(ItemVariant.of(stack), stack.getCount(), transaction);
			if (!simulate) {
				transaction.commit();
				if (this.callback != null && inserted > 0) {
					this.callback.accept(false);
				}
			}
			if (inserted >= stack.getCount()) {
				return ItemStack.EMPTY;
			}
			final ItemStack leftover = stack.copy();
			leftover.setCount(stack.getCount() - (int) inserted);
			return leftover;
		}
	}

	@Override
	public int extractGeneral(final ItemInfoWrapper info, final int amountToExtract, final boolean simulate) {
		if (this.itemHandler != null) {
			final int extractAmount = ItemHelper.extract(this.itemHandler, $ -> $.getItem() == info.type(), ExtractionCountMode.UPTO, amountToExtract, simulate).getCount();
			if (this.callback != null && extractAmount > 0 && !simulate) {
				this.callback.accept(true);
			}
			return extractAmount;
		}

		final ItemStack sample = ItemInfoWrapper.generateFromInfo(info);
		try (final Transaction transaction = Transaction.openOuter()) {
			final long extracted = this.transferStorage.extract(ItemVariant.of(sample), amountToExtract, transaction);
			if (!simulate) {
				transaction.commit();
				if (this.callback != null && extracted > 0) {
					this.callback.accept(true);
				}
			}
			return (int) extracted;
		}
	}

	@Override
	public ItemStack extractSlot(final int index, final int amountToExtract, final boolean simulate) {
		if (this.itemHandler != null) {
			final ItemStack extracted = this.itemHandler.extractItem(index, amountToExtract, simulate);
			if (this.callback != null && !extracted.isEmpty() && !simulate) {
				this.callback.accept(true);
			}
			return extracted;
		}

		int i = 0;
		try (final Transaction transaction = Transaction.openOuter()) {
			for (final StorageView<ItemVariant> view : this.transferStorage) {
				if (i++ != index || view.isResourceBlank()) {
					continue;
				}
				final ItemVariant variant = view.getResource();
				final long extracted = view.extract(variant, amountToExtract, transaction);
				if (!simulate) {
					transaction.commit();
					if (this.callback != null && extracted > 0) {
						this.callback.accept(true);
					}
				}
				return extracted <= 0 ? ItemStack.EMPTY : variant.toStack((int) extracted);
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getContainerSize() {
		if (this.itemHandler != null) {
			return this.itemHandler.getSlots();
		}
		int size = 0;
		for (final StorageView<ItemVariant> ignored : this.transferStorage) {
			size++;
		}
		return size;
	}

	@Override
	public int getMaxStackSize() {
		if (this.itemHandler != null) {
			return this.itemHandler.getSlotLimit(0);
		}
		return 64;
	}

	@Override
	public @NotNull ItemStack getItem(final int slot) {
		if (this.itemHandler != null) {
			return this.itemHandler.getStackInSlot(slot);
		}
		int i = 0;
		for (final StorageView<ItemVariant> view : this.transferStorage) {
			if (i++ == slot) {
				return view.isResourceBlank() ? ItemStack.EMPTY : view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
			}
		}
		return ItemStack.EMPTY;
	}
}
