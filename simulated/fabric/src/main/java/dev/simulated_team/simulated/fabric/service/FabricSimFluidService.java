package dev.simulated_team.simulated.fabric.service;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.simulated_team.simulated.service.SimFluidService;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;

public class FabricSimFluidService implements SimFluidService {

	@Override
	public long mbToLoaderUnits(final long mb) {
		return mb * 81L;
	}

	@Override
	public Fluid getFluidInItem(final ItemStack stack) {
		if (stack.isEmpty()) {
			return null;
		}

		final Storage<FluidVariant> storage = ContainerItemContext.withConstant(stack).find(FluidStorage.ITEM);
		if (storage == null) {
			return null;
		}

		for (final StorageView<FluidVariant> view : storage) {
			if (!view.isResourceBlank() && view.getAmount() > 0) {
				return view.getResource().getFluid();
			}
		}
		return null;
	}

	@Override
	public void fillCreateFluidTank(final Object tankBlockEntity, final Fluid fluid, final int millibuckets) {
		if (tankBlockEntity instanceof final FluidTankBlockEntity be) {
			TransferUtil.insertFluid(be.getFluidStorage(null), new FluidStack(fluid, millibuckets * 81L));
		}
	}
}
