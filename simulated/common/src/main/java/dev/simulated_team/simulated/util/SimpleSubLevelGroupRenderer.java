package dev.simulated_team.simulated.util;

import dev.ryanhcode.sable.Sable;
import dev.ryanhcode.sable.companion.math.BoundingBox3d;
import dev.ryanhcode.sable.sublevel.ClientSubLevel;
import dev.ryanhcode.sable.sublevel.SubLevel;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;

import java.util.Collection;

public class SimpleSubLevelGroupRenderer {
    public static boolean RENDERING_SIMPLE = false;

    public static Collection<ClientSubLevel> getRenderedChain(final ClientSubLevel subLevel) {
        final ObjectOpenHashSet<ClientSubLevel> visited = new ObjectOpenHashSet<>();
        final ObjectOpenHashSet<ClientSubLevel> frontier = new ObjectOpenHashSet<>();

        frontier.add(subLevel);

        while (!frontier.isEmpty()) {
            final ClientSubLevel current = frontier.iterator().next();

            frontier.remove(current);
            visited.add(current);

            final Iterable<SubLevel> intersecting = Sable.HELPER.getAllIntersecting(current.getLevel(), new BoundingBox3d(current.boundingBox()));

            for (final SubLevel neighbor : intersecting) {
                final ClientSubLevel serverNeighbor = (ClientSubLevel) neighbor;

                if (!visited.contains(serverNeighbor)) {
                    frontier.add(serverNeighbor);
                }
            }
        }

        return visited;
    }

    public static void renderChain(final SubLevel subLevel, final AdvancedFbo fbo, final Matrix4f modelView, final Matrix4f projectionMat, final Vector3d cameraPosition, final Quaternionf orientation, final float partialTicks) {
        final ClientSubLevel clientSubLevel = (ClientSubLevel) subLevel;
        final ClientLevel level = clientSubLevel.getLevel();
        final Collection<ClientSubLevel> subLevels = SimpleSubLevelGroupRenderer.getRenderedChain(clientSubLevel);

        renderGroup(level, subLevels, fbo, modelView, projectionMat, cameraPosition, orientation, partialTicks, true);
    }

    public static void renderGroup(final ClientLevel level, final Collection<ClientSubLevel> subLevels, final AdvancedFbo fbo, final Matrix4f modelView, final Matrix4f projectionMat, final Vector3d cameraPosition, final Quaternionf orientation, final float partialTicks, final boolean renderPlayers) {
        final MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        bufferSource.endBatch();
        AdvancedFbo.unbind();
    }
}
