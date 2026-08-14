package dev.ryanhcode.offroad.neoforge;


import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.data.OffroadAdvancementTriggers;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import dev.ryanhcode.offroad.index.OffroadAdvancements;
import dev.ryanhcode.offroad.neoforge.data.OffroadDatagen;
import dev.ryanhcode.offroad.neoforge.service.NeoForgeOffroadConfigService;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Offroad.MOD_ID)
public class OffroadNeoForge {
    public OffroadNeoForge() {
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        this.modBusRegistry(modBus);

        Offroad.init();

        NeoForgeOffroadConfigService.register();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> OffroadNeoForgeClient.init(modBus));
    }

    private void modBusRegistry(final IEventBus modBus) {
        modBus.register(NeoForgeOffroadConfigService.class);

        modBus.addListener(OffroadNeoForge::init);
        modBus.addListener(EventPriority.HIGHEST, OffroadDatagen::gatherDataHighPriority);
        modBus.addListener(EventPriority.LOWEST, OffroadDatagen::gatherData);
        modBus.addListener(OffroadDatagen::registerEvent);
        MinecraftForge.EVENT_BUS.addListener((TickEvent.LevelTickEvent event) -> {
            if (event.phase == TickEvent.Phase.END) {
                OffroadCommonEvents.tickLevelEvent(event.level);
            }
        });

        Offroad.getRegistrate().registerEventListeners(modBus);
    }

    private static void init(final FMLCommonSetupEvent event) {
        OffroadAdvancements.init();
        OffroadAdvancementTriggers.register();
        OffroadCommonEvents.modifyDefaultComponents();
    }
}
