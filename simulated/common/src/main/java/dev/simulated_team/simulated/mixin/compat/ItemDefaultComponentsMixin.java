package dev.simulated_team.simulated.mixin.compat;

import dev.simulated_team.simulated.compat.ItemComponents;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Item.class)
public class ItemDefaultComponentsMixin {
    @Inject(method = "<init>(Lnet/minecraft/world/item/Item$Properties;)V", at = @At("RETURN"))
    private void simulated$applyDefaultComponents(final Item.Properties properties, final CallbackInfo ci) {
        ItemComponents.applyPending((Item) (Object) this, properties);
    }
}
