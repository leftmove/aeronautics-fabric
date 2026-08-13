package dev.simulated_team.simulated.service;

import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;

public interface SimDiagramFlywheelService {
	SimDiagramFlywheelService INSTANCE = ServiceUtil.load(SimDiagramFlywheelService.class);

	void renderEmbeddedBlockEntities(
			VisualizationManager visualizationManager,
			Collection<ClientSubLevel> subLevels,
			VanillaSubLevelBlockEntityRenderer beRenderer,
			Vec3 cameraPosition,
			float partialTicks);
}
