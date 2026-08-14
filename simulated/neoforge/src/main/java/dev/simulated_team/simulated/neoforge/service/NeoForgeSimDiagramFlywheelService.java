package dev.simulated_team.simulated.neoforge.service;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.engine_room.flywheel.api.visualization.VisualizationManager;
import dev.ryanhcode.sable.mixinhelpers.sublevel_render.vanilla.VanillaSubLevelBlockEntityRenderer;
import dev.ryanhcode.sable.mixinterface.BlockEntityRenderDispatcherExtension;
import dev.ryanhcode.sable.neoforge.mixinhelper.compatibility.flywheel.SubLevelEmbedding;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.render.SubLevelRenderData;
import dev.simulated_team.simulated.mixin_interface.diagram.VisualManagerExtension;
import dev.simulated_team.simulated.service.SimDiagramFlywheelService;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Collection;

public class NeoForgeSimDiagramFlywheelService implements SimDiagramFlywheelService {
	@Override
	public void renderEmbeddedBlockEntities(
			final VisualizationManager visualizationManager,
			final Collection<ClientSubLevel> subLevels,
			final VanillaSubLevelBlockEntityRenderer beRenderer,
			final Vec3 cameraPosition,
			final float partialTicks
	) {
		if (!(visualizationManager.blockEntities() instanceof final VisualManagerExtension visualManager)) {
			return;
		}

		for (final ClientSubLevel beSubLevel : subLevels) {
			final BlockEntityRenderDispatcherExtension dispatcher = (BlockEntityRenderDispatcherExtension) beRenderer.getBlockEntityRenderDispatcher();
			final SubLevelEmbedding embeddingInfo = visualManager.sable$getBEEmbeddingInfo(beSubLevel);
			if (embeddingInfo == null) {
				continue;
			}

			final Vector3d chunkOffset = new Vector3d();
			final Matrix4f transformation = new Matrix4f();
			final Matrix4f transformationInverse = new Matrix4f();
			final SubLevelRenderData data = beSubLevel.getRenderData();

			beSubLevel.renderPose().rotationPoint().negate(chunkOffset.zero());
			data.getTransformation(cameraPosition.x, cameraPosition.y, cameraPosition.z, transformation);

			final Vector3f c = transformation.invert(transformationInverse).transformPosition(new Vector3f());
			dispatcher.sable$setCameraPosition(new Vec3(c.x - chunkOffset.x(), c.y - chunkOffset.y(), c.z - chunkOffset.z()));

			final PoseStack beMatrices = new PoseStack();
			beMatrices.pushPose();
			beMatrices.last().pose().mul(transformation);
			beRenderer.renderBlockEntities(embeddingInfo.blockEntities(), beMatrices, partialTicks, -chunkOffset.x, -chunkOffset.y, -chunkOffset.z);
			beMatrices.popPose();

			dispatcher.sable$setCameraPosition(null);
		}
	}
}
