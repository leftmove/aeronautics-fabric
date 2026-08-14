package dev.simulated_team.simulated.mixin.end_sea;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.simulated_team.simulated.content.end_sea.EndSeaRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.LightTexture;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    public void renderLevel(final PoseStack poseStack, final float partialTick, final long finishNanoTime, final boolean renderBlockOutline, final Camera camera, final GameRenderer gameRenderer, final LightTexture lightTexture, final Matrix4f projectionMatrix, final CallbackInfo ci) {
        EndSeaRenderer.render(camera, gameRenderer);
    }
}
