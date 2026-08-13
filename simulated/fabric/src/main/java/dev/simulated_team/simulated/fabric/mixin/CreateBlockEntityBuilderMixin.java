package dev.simulated_team.simulated.fabric.mixin;

import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import dev.simulated_team.simulated.fabric.FabricVisualizerQueue;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CreateBlockEntityBuilder.class)
public class CreateBlockEntityBuilderMixin {
	@Inject(method = "registerVisualizer", at = @At("HEAD"), cancellable = true, remap = false)
	private void simulated$registerVisualizer(final CallbackInfo ci) {
		FabricVisualizerQueue.queue((CreateBlockEntityBuilder<?, ?>) (Object) this);
		ci.cancel();
	}
}
