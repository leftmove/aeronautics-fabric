package dev.ryanhcode.offroad.fabric.service;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.service.OffroadMountedStorageService;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;

public class FabricOffroadMountedStorageService implements OffroadMountedStorageService {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends MountedStorageManager & BoreheadAttachedStorage> T getSidedBoreheadContraptionMountedStorage() {
		return (T) new FabricBoreheadBearingMountedStorage();
	}

	public static class FabricBoreheadBearingMountedStorage extends MountedStorageManager implements BoreheadAttachedStorage {
		public WeakReference<BoreheadBearingBlockEntity> attachedBoreheadBearing = new WeakReference<>(null);
		private boolean insertAllowed;

		@Override
		public void initialize() {
			super.initialize();
			this.items = new FabricBoreheadInvWrapper(this.items);
			this.allItems = this.items;
			if (this.fuelItems != null) {
				this.fuelItems = new FabricBoreheadInvWrapper(this.fuelItems);
			}
		}

		@Override
		public void attachBlockEntity(final BoreheadBearingBlockEntity be) {
			this.attachedBoreheadBearing = new WeakReference<>(be);
		}

		@Override
		public void setInsertAllowed(final boolean insertionAllowed) {
			this.insertAllowed = insertionAllowed;
		}

		@Override
		public void invokeUnstall() {
			final BoreheadBearingBlockEntity bbe = this.attachedBoreheadBearing.get();
			if (bbe != null) {
				bbe.startUnstalling();
			}
		}

		class FabricBoreheadInvWrapper extends MountedItemStorageWrapper {
			FabricBoreheadInvWrapper(final MountedItemStorageWrapper wrapped) {
				super(wrapped.storages);
			}

			@Override
			public @NotNull ItemStack insertItem(final int slot, final @NotNull ItemStack stack, final boolean simulate) {
				if (FabricBoreheadBearingMountedStorage.this.insertAllowed) {
					return super.insertItem(slot, stack, simulate);
				}
				return stack;
			}

			@Override
			public @NotNull ItemStack extractItem(final int slot, final int amount, final boolean simulate) {
				final BoreheadBearingBlockEntity bbe = FabricBoreheadBearingMountedStorage.this.attachedBoreheadBearing.get();
				if (bbe != null) {
					final ItemStack extracted = super.extractItem(slot, amount, simulate);
					if (!extracted.isEmpty()) {
						bbe.startUnstalling();
						return extracted;
					}
				}
				return ItemStack.EMPTY;
			}
		}
	}
}
