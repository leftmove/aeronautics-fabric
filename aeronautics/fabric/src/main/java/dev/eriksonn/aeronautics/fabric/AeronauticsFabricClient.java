package dev.eriksonn.aeronautics.fabric;

import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.AeronauticsClient;
import dev.eriksonn.aeronautics.events.AeronauticsClientEvents;
import dev.eriksonn.aeronautics.fabric.content.fluids.levitite.LevititeBlendFluid;
import dev.eriksonn.aeronautics.fabric.index.AeroParticleTypesFabric;
import dev.eriksonn.aeronautics.index.AeroBlocks;
import dev.eriksonn.aeronautics.util.AeroColors;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.fabricmc.fabric.api.client.render.fluid.v1.SimpleFluidRenderHandler;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class AeronauticsFabricClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AeroParticleTypesFabric.registerClient();
		AeronauticsClient.init();

		ClientTickEvents.START_CLIENT_TICK.register(client -> AeronauticsClientEvents.clientLevelTick(false));
		ClientTickEvents.END_CLIENT_TICK.register(client -> AeronauticsClientEvents.clientLevelTick(true));

		final ResourceLocation still = Aeronautics.path("fluid/levitite_blend_still");
		final ResourceLocation flowing = Aeronautics.path("fluid/levitite_blend_flow");
		FluidRenderHandlerRegistry.INSTANCE.register(LevititeBlendFluid.SOURCE, LevititeBlendFluid.FLOWING,
				new SimpleFluidRenderHandler(still, flowing, AeroColors.LEVIBLEND_THE_FOG_IS_COMING));

		BlockRenderLayerMap.INSTANCE.putFluids(RenderType.translucent(), LevititeBlendFluid.SOURCE, LevititeBlendFluid.FLOWING);
		BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.solid(), AeroBlocks.LEVITITE.get(), AeroBlocks.PEARLESCENT_LEVITITE.get());
	}
}
