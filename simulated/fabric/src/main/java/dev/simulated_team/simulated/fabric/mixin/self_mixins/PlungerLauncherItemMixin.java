package dev.simulated_team.simulated.fabric.mixin.self_mixins;

import dev.simulated_team.simulated.content.items.plunger_launcher.PlungerLauncherItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlungerLauncherItemMixin {
	@Inject(method = "swing(Lnet/minecraft/world/InteractionHand;Z)V", at = @At("HEAD"), cancellable = true)
	private void simulated$cancelPlungerSwing(final InteractionHand hand, final boolean updateSelf, final CallbackInfo ci) {
		final ItemStack stack = ((LivingEntity) (Object) this).getItemInHand(hand);
		if (stack.getItem() instanceof PlungerLauncherItem) {
			ci.cancel();
		}
	}
}
