package dev.ryanhcode.offroad.fabric.service;

import com.simibubi.create.api.contraption.storage.item.MountedItemStorageWrapper;
import com.simibubi.create.content.contraptions.MountedStorageManager;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadAttachedStorage;
import dev.ryanhcode.offroad.content.blocks.borehead_bearing.BoreheadBearingBlockEntity;
import dev.ryanhcode.offroad.service.OffroadMountedStorageService;
import io.github.fabricators_of_create.porting_lib.transfer.callbacks.TransactionCallback;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;

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
			public long insert(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
				if (!FabricBoreheadBearingMountedStorage.this.insertAllowed) {
					return 0;
				}
				return super.insert(resource, maxAmount, transaction);
			}

			@Override
			public long insertSlot(final int slot, final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
				if (!FabricBoreheadBearingMountedStorage.this.insertAllowed) {
					return 0;
				}
				return super.insertSlot(slot, resource, maxAmount, transaction);
			}

			@Override
			public long extract(final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
				final BoreheadBearingBlockEntity bbe = FabricBoreheadBearingMountedStorage.this.attachedBoreheadBearing.get();
				if (bbe == null) {
					return 0;
				}
				final long extracted = super.extract(resource, maxAmount, transaction);
				if (extracted > 0) {
					TransactionCallback.onSuccess(transaction, bbe::startUnstalling);
				}
				return extracted;
			}

			@Override
			public long extractSlot(final int slot, final ItemVariant resource, final long maxAmount, final TransactionContext transaction) {
				final BoreheadBearingBlockEntity bbe = FabricBoreheadBearingMountedStorage.this.attachedBoreheadBearing.get();
				if (bbe == null) {
					return 0;
				}
				final long extracted = super.extractSlot(slot, resource, maxAmount, transaction);
				if (extracted > 0) {
					TransactionCallback.onSuccess(transaction, bbe::startUnstalling);
				}
				return extracted;
			}
		}
	}
}
