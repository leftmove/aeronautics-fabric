package dev.simulated_team.simulated.neoforge.events;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.command.SimCommand;
import dev.simulated_team.simulated.compat.ItemComponents;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import dev.simulated_team.simulated.data.advancements.SimAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.data.neoforge.SimProcessingRecipeGen;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.events.SimulatedCommonEvents;
import dev.simulated_team.simulated.index.SimArmInteractions;
import dev.simulated_team.simulated.index.SimSoundEvents;
import dev.simulated_team.simulated.index.SimTags;
import dev.simulated_team.simulated.index.neoforge.NeoForgeSimStats;
import dev.simulated_team.simulated.multiloader.energy.SingleBattery;
import dev.simulated_team.simulated.multiloader.energy.SingleBatteryWrapper;
import dev.simulated_team.simulated.multiloader.inventory.AbstractContainer;
import dev.simulated_team.simulated.multiloader.inventory.neoforge.ContainerWrapper;
import dev.simulated_team.simulated.multiloader.tanks.SingleTank;
import dev.simulated_team.simulated.multiloader.tanks.neoforge.SingleTankWrapper;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimConfigService;
import dev.simulated_team.simulated.neoforge.service.NeoForgeSimInventoryService;
import net.createmod.catnip.config.ConfigBase;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Simulated.MOD_ID)
public class SimNeoForgeCommonEvents {

    @SubscribeEvent
    public static void loadChunk(final ChunkEvent.Load event) {
        SimulatedCommonEvents.onChunkLoad(event.getLevel(), event.getChunk(), event.isNewChunk());
    }

    @SubscribeEvent
    public static void playerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        final Player player = event.getEntity();
        SimulatedCommonEvents.onPlayerLoggedIn(player);
    }

    @SubscribeEvent
    public static void registerCommands(final RegisterCommandsEvent event) {
        SimCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    @SubscribeEvent
    public static void serverStopped(final ServerStoppedEvent event) {
        SimulatedCommonEvents.onServerStopped(event.getServer());
    }

    @SubscribeEvent
    public static void postServerTick(final TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        final MinecraftServer server = event.getServer();
        for (final ServerLevel level : server.getAllLevels()) {
            SimulatedCommonEvents.onServerTickEnd(level);
        }
    }

    @SubscribeEvent
    public static void syncDataPack(final OnDatapackSyncEvent event) {
        EndSeaPhysicsData.syncDataPacket(foundry.veil.api.network.VeilPacketManager.all(event.getPlayerList().getServer()));
    }

    @SubscribeEvent
    public static void addReloadListeners(final AddReloadListenerEvent event) {
        event.addListener(EndSeaPhysicsData.ReloadListener.INSTANCE);
    }

    @SubscribeEvent
    public static void keyInput(final InputEvent.InteractionKeyMappingTriggered event) {
        if (event.isUseItem()) {
            if (SimulatedCommonClientEvents.useItemMappingTriggered()) {
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        }
    }

    @SubscribeEvent
    public static void rightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        final InteractionResult result = SimulatedCommonEvents.rightClickBlock(event.getLevel(), event.getPos(), event.getEntity(), event.getItemStack());
        if (result != null) {
            event.setCancellationResult(result);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLivingEntityUseItem(final PlayerInteractEvent.RightClickItem event) {
        final LivingEntity entity = event.getEntity();
        if (entity instanceof final Player player && player.isLocalPlayer()) {
            SimulatedCommonClientEvents.useItemOnAirEvent(entity.level(), player, event.getItemStack(), event.getHand());
        }
    }

    @SubscribeEvent
    public static void attachCapabilities(final AttachCapabilitiesEvent<BlockEntity> event) {
        final BlockEntity be = event.getObject();

        for (final NeoForgeSimInventoryService.InventoryGetterHolder<? extends BlockEntity> getter : NeoForgeSimInventoryService.inventoryGetters) {
            if (getter.type() == be.getType()) {
                event.addCapability(Simulated.path("items"), capabilityProvider(ForgeCapabilities.ITEM_HANDLER, side -> {
                    final AbstractContainer container = getter.castBlockEntityAndGetInv(be, side);
                    return container == null ? null : new ContainerWrapper<>(container);
                }));
            }
        }

        for (final NeoForgeSimInventoryService.TankGetterHolder<? extends BlockEntity> getter : NeoForgeSimInventoryService.fluidTankGetters) {
            if (getter.type() == be.getType()) {
                event.addCapability(Simulated.path("fluids"), capabilityProvider(ForgeCapabilities.FLUID_HANDLER, side -> {
                    final SingleTank container = getter.castBlockEntityAndGetInv(be, side);
                    return container == null ? null : new SingleTankWrapper(container);
                }));
            }
        }

        for (final NeoForgeSimInventoryService.EnergyGetterHolder<? extends BlockEntity> getter : NeoForgeSimInventoryService.energyGetters) {
            if (getter.type() == be.getType()) {
                event.addCapability(Simulated.path("energy"), capabilityProvider(ForgeCapabilities.ENERGY, side -> {
                    final SingleBattery battery = getter.castBlockEntityAndGetInv(be, side);
                    return battery == null ? null : new SingleBatteryWrapper(battery);
                }));
            }
        }
    }

    private static <C> ICapabilityProvider capabilityProvider(final Capability<C> capability, final java.util.function.Function<Direction, C> factory) {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(final Capability<T> cap, final Direction side) {
                if (cap != capability) {
                    return LazyOptional.empty();
                }
                final C value = factory.apply(side);
                return value == null ? LazyOptional.empty() : LazyOptional.of(() -> value).cast();
            }
        };
    }

    public static class ModBusEvents {

        @SubscribeEvent
        public static void commonSetup(final FMLCommonSetupEvent event) {
            SimArmInteractions.init();
            SimAdvancements.register();
            SimAdvancementTriggers.register();
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void gatherDataHighPriority(final GatherDataEvent event) {
            SimTags.addGenerators();
        }

        @SubscribeEvent
        public static void gatherData(final GatherDataEvent event) {
            final DataGenerator generator = event.getGenerator();

            final PackOutput output = generator.getPackOutput();
            final CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

            if (event.includeClient()) {
                generator.addProvider(true, SimSoundEvents.REGISTRY.getProvider(output));
            }

            generator.addProvider(event.includeServer(), new SimAdvancements(output, lookupProvider));
            generator.addProvider(event.includeServer(), SimProcessingRecipeGen.registerAll(output, lookupProvider));
        }

        @SubscribeEvent
        public static void loadConfig(final ModConfigEvent.Loading event) {
            for (final ConfigBase config : NeoForgeSimConfigService.CONFIGS.values()) {
                if (config.specification == event.getConfig().getSpec()) {
                    config.onLoad();
                }
            }
        }

        @SubscribeEvent
        public static void reloadConfig(final ModConfigEvent.Reloading event) {
            for (final ConfigBase config : NeoForgeSimConfigService.CONFIGS.values()) {
                if (config.specification == event.getConfig().getSpec()) {
                    config.onReload();
                }
            }
        }

        @SubscribeEvent
        public static void postRegister(final FMLLoadCompleteEvent event) {
            NeoForgeSimStats.bootstrap();
            SimulatedCommonEvents.modifyDefaultComponents((itemLike, patchConsumer) -> applyDefaultComponents(itemLike, patchConsumer));
        }
    }

    private static void applyDefaultComponents(final ItemLike itemLike, final Consumer<DataComponentPatch.Builder> patchConsumer) {
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();
        patchConsumer.accept(builder);
        for (final var entry : builder.build().entrySet()) {
            entry.getValue().ifPresent(value -> setDefault(itemLike, entry.getKey(), value));
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void setDefault(final ItemLike itemLike, final DataComponentType<?> type, final Object value) {
        ItemComponents.setDefault(itemLike, (DataComponentType<T>) type, (T) value);
    }
}
