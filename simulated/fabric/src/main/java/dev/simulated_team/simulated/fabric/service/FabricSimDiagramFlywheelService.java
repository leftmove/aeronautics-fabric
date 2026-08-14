package dev.simulated_team.simulated.fabric.service;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.simulated_team.simulated.service.SimDiagramFlywheelService;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public class FabricSimDiagramFlywheelService implements SimDiagramFlywheelService {
	@Override
	public void renderEmbeddedBlockEntities(
			final VisualizationManager visualizationManager,
			final Collection<ClientSubLevel> subLevels,
			final VanillaSubLevelBlockEntityRenderer beRenderer,
			final Vec3 cameraPosition,
			final float partialTicks
	) {
	}
}
