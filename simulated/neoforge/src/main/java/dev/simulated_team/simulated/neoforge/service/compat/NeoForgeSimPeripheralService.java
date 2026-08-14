package dev.simulated_team.simulated.neoforge.service.compat;

import dan200.computercraft.api.ForgeComputerCraftAPI;
import dan200.computercraft.api.network.wired.WiredElement;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.simulated_team.simulated.service.compat.SimPeripheralService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class NeoForgeSimPeripheralService implements SimPeripheralService {

    private static final List<Entry<BlockEntity, IPeripheral>> PERIPHERALS = new ArrayList<>();
    private static final List<Entry<BlockEntity, WiredElement>> WIRED_ELEMENTS = new ArrayList<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> void addPeripheral(final Supplier<BlockEntityType<T>> typeSupplier, final CapabilityGetter<T, IPeripheral> getter) {
        PERIPHERALS.add((Entry<BlockEntity, IPeripheral>) new Entry<>(typeSupplier, getter));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BlockEntity> void addWired(Supplier<BlockEntityType<T>> typeSupplier, CapabilityGetter<T, WiredElement> getter) {
        WIRED_ELEMENTS.add((Entry<BlockEntity, WiredElement>) new Entry<>(typeSupplier, getter));
    }

    @SubscribeEvent
    public static void commonSetup(final FMLCommonSetupEvent event) {
        ForgeComputerCraftAPI.registerPeripheralProvider((final Level level, final BlockPos pos, final Direction side) -> {
            final BlockEntity be = level.getBlockEntity(pos);
            if (be == null) {
                return LazyOptional.empty();
            }
            for (final Entry<BlockEntity, IPeripheral> entry : PERIPHERALS) {
                if (entry.typeSupplier.get() == be.getType()) {
                    final IPeripheral peripheral = entry.peripheralFunction().get(be, side);
                    return peripheral == null ? LazyOptional.empty() : LazyOptional.of(() -> peripheral);
                }
            }
            return LazyOptional.empty();
        });
    }

    private record Entry<T extends BlockEntity, V>(Supplier<BlockEntityType<T>> typeSupplier,
                                                   CapabilityGetter<T, V> peripheralFunction) {
    }
}
