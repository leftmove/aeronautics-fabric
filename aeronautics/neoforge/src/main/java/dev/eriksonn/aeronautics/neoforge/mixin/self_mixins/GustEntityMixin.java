package dev.eriksonn.aeronautics.neoforge.mixin.self_mixins;

import dev.eriksonn.aeronautics.content.blocks.hot_air.gust.GustEntity;
import net.neoforged.neoforge.entity.IEntityWithComplexSpawn;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GustEntity.class)
public abstract class GustEntityMixin implements IEntityWithComplexSpawn {
}
