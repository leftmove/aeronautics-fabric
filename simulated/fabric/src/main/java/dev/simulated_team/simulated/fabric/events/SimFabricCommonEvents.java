package dev.simulated_team.simulated.fabric.events;

import dev.simulated_team.simulated.command.SimCommand;
import dev.simulated_team.simulated.compat.ItemComponents;
import dev.simulated_team.simulated.content.end_sea.EndSeaPhysicsData;
import dev.simulated_team.simulated.data.advancements.SimAdvancementTriggers;
import dev.simulated_team.simulated.data.advancements.SimAdvancements;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.events.SimulatedCommonEvents;
import dev.simulated_team.simulated.fabric.service.FabricSimInventoryService;
import dev.simulated_team.simulated.fabric.service.FabricSimPlatformService;
import dev.simulated_team.simulated.fabric.service.compat.FabricSimPeripheralService;
import dev.simulated_team.simulated.index.SimArmInteractions;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

public final class SimFabricCommonEvents {
	private SimFabricCommonEvents() {
	}

	public static void register() {
		ServerChunkEvents.CHUNK_LOAD.register((world, chunk) -> SimulatedCommonEvents.onChunkLoad(world, chunk, false));
		ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> SimulatedCommonEvents.onPlayerLoggedIn(handler.player));
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> SimCommand.register(dispatcher, registryAccess));
		ServerLifecycleEvents.SERVER_STARTED.register(FabricSimPlatformService::setServer);
		ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
			FabricSimPlatformService.setServer(null);
			SimulatedCommonEvents.onServerStopped(server);
		});
		ServerTickEvents.END_WORLD_TICK.register(level -> {
			if (level instanceof net.minecraft.server.level.ServerLevel serverLevel) {
				SimulatedCommonEvents.onServerTickEnd(serverLevel);
			}
		});
		ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) ->
				EndSeaPhysicsData.syncDataPacket(foundry.veil.api.network.VeilPacketManager.player(player)));

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(wrap(EndSeaPhysicsData.ReloadListener.ID, EndSeaPhysicsData.ReloadListener.INSTANCE));

		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			final ItemStack stack = player.getItemInHand(hand);
			final InteractionResult result = SimulatedCommonEvents.rightClickBlock(world, hitResult.getBlockPos(), player, stack);
			if (result != null) {
				return result;
			}

			if (world.isClientSide && player.isLocalPlayer()) {
				final InteractionResult client = SimulatedCommonClientEvents.onRightClickBlock(player, hand, hitResult.getBlockPos(), hitResult);
				if (client != null) {
					return client;
				}
				if (stack.is(SimItems.HONEY_GLUE.get())) {
					SimClickInteractions.HONEY_GLUE_MANAGER.selectPos(hitResult.getBlockPos(), player, stack);
					return InteractionResult.SUCCESS;
				}
				if (SimulatedCommonClientEvents.useItemOnBlockEvent(world, player, stack, hand)) {
					return InteractionResult.CONSUME;
				}
				if (HoldInteractionManager.isActive()) {
					return InteractionResult.FAIL;
				}
			}
			return InteractionResult.PASS;
		});

		UseItemCallback.EVENT.register((player, world, hand) -> {
			if (player.isLocalPlayer()) {
				SimulatedCommonClientEvents.useItemOnAirEvent(world, player, player.getItemInHand(hand), hand);
			}
			if (world.isClientSide && SimulatedCommonClientEvents.useItemMappingTriggered()) {
				return InteractionResultHolder.fail(player.getItemInHand(hand));
			}
			return InteractionResultHolder.pass(player.getItemInHand(hand));
		});

		SimulatedCommonEvents.modifyDefaultComponents((itemLike, patchConsumer) -> applyDefaultComponents(itemLike, patchConsumer));

		SimArmInteractions.init();
		SimAdvancements.register();
		SimAdvancementTriggers.register();
		FabricSimInventoryService.registerLookups();
		FabricSimPeripheralService.registerLookups();
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

	public static IdentifiableResourceReloadListener wrap(final ResourceLocation id, final PreparableReloadListener listener) {
		return new IdentifiableResourceReloadListener() {
			@Override
			public ResourceLocation getFabricId() {
				return id;
			}

			@Override
			public CompletableFuture<Void> reload(final PreparableReloadListener.PreparationBarrier barrier, final ResourceManager manager, final ProfilerFiller prepareProfiler, final ProfilerFiller applyProfiler, final Executor prepareExecutor, final Executor applyExecutor) {
				return listener.reload(barrier, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
			}
		};
	}
}
