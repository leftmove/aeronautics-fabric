package dev.simulated_team.simulated.neoforge.events;

import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterItemBindHandler;
import dev.simulated_team.simulated.events.SimulatedCommonClientEvents;
import dev.simulated_team.simulated.index.SimClickInteractions;
import dev.simulated_team.simulated.index.SimItems;
import dev.simulated_team.simulated.index.SimKeys;
import dev.simulated_team.simulated.neoforge.service.SimpleResourceManagerRegistryService;
import dev.simulated_team.simulated.util.hold_interaction.HoldInteractionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = Simulated.MOD_ID, value = Dist.CLIENT)
public class SimNeoForgeClientEvents {

    @SubscribeEvent
    public static void clientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            SimulatedCommonClientEvents.preClientTick(Minecraft.getInstance());
        } else if (event.phase == TickEvent.Phase.END) {
            SimulatedCommonClientEvents.postClientTick(Minecraft.getInstance());
        }
    }

    @SubscribeEvent
    public static void postRenderGui(final RenderGuiEvent.Post event) {
        SimulatedCommonClientEvents.renderOverlays(event.getGuiGraphics(), event.getPartialTick());
    }

    @SubscribeEvent
    public static void keyInput(final InputEvent.Key event) {
        SimulatedCommonClientEvents.onAfterKeyPress(event.getKey(), event.getScanCode(), event.getAction(), event.getModifiers());
    }

    @SubscribeEvent
    public static void postMouseButtonInput(final InputEvent.MouseButton.Post event) {
        SimulatedCommonClientEvents.onAfterMouseInput(event.getButton(), event.getModifiers(), event.getAction());
    }

    @SubscribeEvent
    public static void playerInteractRightClickBlock(final PlayerInteractEvent.RightClickBlock event) {
        if (event.getEntity().isLocalPlayer()) {
            final InteractionResult res = SimulatedCommonClientEvents.onRightClickBlock(event.getEntity(), event.getHand(), event.getPos(), event.getHitVec());

            if (res != null) {
                event.setCancellationResult(res);
                event.setCanceled(true);
                return;
            }

            if (SimulatedCommonClientEvents.useItemOnBlockEvent(event.getLevel(), event.getEntity(), event.getItemStack(), event.getHand())) {
                event.setCancellationResult(InteractionResult.CONSUME);
                event.setCanceled(true);
                return;
            }

            if (HoldInteractionManager.isActive()) {
                event.setCanceled(true);
                return;
            }
        }

        if (event.getItemStack().is(SimItems.HONEY_GLUE.get())) {
            event.setUseBlock(Event.Result.DENY);
            if (event.getLevel().isClientSide) {
                SimClickInteractions.HONEY_GLUE_MANAGER.selectPos(event.getPos(), event.getEntity(), event.getItemStack());
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void itemTooltip(final ItemTooltipEvent event) {
        SimulatedCommonClientEvents.appendTooltip(event.getItemStack(), event.getFlags(), event.getEntity(), event.getToolTip());
    }

    public static class ModBusEvents {

        @SubscribeEvent
        public static void registerKeyMappings(final RegisterKeyMappingsEvent event) {
            SimKeys.registerTo(event::register);
        }

        @SubscribeEvent
        public static void registerGuiOverlays(final RegisterGuiOverlaysEvent event) {
            event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "linked_typewriter_binding", LinkedTypewriterItemBindHandler.OVERLAY);
        }

        @SubscribeEvent
        public static void addReloadListener(final RegisterClientReloadListenersEvent event) {
            for (final PreparableReloadListener listener : SimpleResourceManagerRegistryService.LISTENERS) {
                event.registerReloadListener(listener);
            }
        }
    }
}
