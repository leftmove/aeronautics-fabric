package dev.simulated_team.simulated.neoforge.service;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import dev.simulated_team.simulated.service.SimFluidService;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class NeoForgeSimFluidService implements SimFluidService {
    public long mbToLoaderUnits(final long mb) {
        return mb;
    }

    @Override
    public Fluid getFluidInItem(final ItemStack stack) {
        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).map(handler -> {
            final FluidStack fluid = handler.getFluidInTank(0);
            return fluid.isEmpty() ? null : fluid.getFluid();
        }).orElse(null);
    }

    @Override
    public void fillCreateFluidTank(final Object tankBlockEntity, final Fluid fluid, final int millibuckets) {
        if (tankBlockEntity instanceof final FluidTankBlockEntity be) {
            be.getTankInventory().fill(new FluidStack(fluid, millibuckets), IFluidHandler.FluidAction.EXECUTE);
        }
    }
}
