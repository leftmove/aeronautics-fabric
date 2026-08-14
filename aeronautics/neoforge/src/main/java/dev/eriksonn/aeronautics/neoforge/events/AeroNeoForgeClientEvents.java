package dev.eriksonn.aeronautics.neoforge.events;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.events.AeronauticsClientEvents;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.index.client.AeroRenderTypes;
import dev.eriksonn.aeronautics.mixin.levitite.ChunkRenderTypeSetAccessor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.List;

@EventBusSubscriber(modid = Aeronautics.MOD_ID, value = Dist.CLIENT)
public class AeroNeoForgeClientEvents {

    @SubscribeEvent
    public static void clientTick(final TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            AeronauticsClientEvents.clientLevelTick(false);
        } else if (event.phase == TickEvent.Phase.END) {
            AeronauticsClientEvents.clientLevelTick(true);
        }
    }

    public static class ModBusEvents {

        @SubscribeEvent
        public static void clientSetup(final FMLClientSetupEvent event) {
            final ChunkRenderTypeSet set = ChunkRenderTypeSet.of(RenderType.solid(), AeroRenderTypes.levitite(), AeroRenderTypes.levititeGhosts());
            ItemBlockRenderTypes.setRenderLayer(AeroBlocks.LEVITITE.get(), set);
            ItemBlockRenderTypes.setRenderLayer(AeroBlocks.PEARLESCENT_LEVITITE.get(), set);

            fixChunkRenderTypeSet();
        }

        private static void fixChunkRenderTypeSet() {
            final List<RenderType> list = RenderType.chunkBufferLayers();

            ChunkRenderTypeSetAccessor.setChunkRenderTypesList(list);
            ChunkRenderTypeSetAccessor.setChunkRenderTypes(list.toArray(new RenderType[0]));
            ((ChunkRenderTypeSetAccessor) (Object) ChunkRenderTypeSet.all()).getBits().set(0, list.size());
        }

        @SubscribeEvent
        public static void registerRegisterStageEvent(final RenderLevelStageEvent.RegisterStageEvent event) {
            event.register(Aeronautics.path("levitite"), AeroRenderTypes.levitite());
            event.register(Aeronautics.path("levitite_ghosts"), AeroRenderTypes.levititeGhosts());
        }
    }
}
