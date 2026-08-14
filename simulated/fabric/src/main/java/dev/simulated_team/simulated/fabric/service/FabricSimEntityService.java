package dev.simulated_team.simulated.fabric.service;

import com.tterrag.registrate.builders.EntityBuilder;
import dev.simulated_team.simulated.fabric.entity.PersistentDataHolder;
import dev.simulated_team.simulated.index.SimEntityTypes;
import dev.simulated_team.simulated.service.SimEntityService;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class FabricSimEntityService implements SimEntityService {

	@Override
	public CompoundTag getCustomData(final Entity entity) {
		return ((PersistentDataHolder) entity).simulated$getPersistentData();
	}

	@Override
	public double getPlayerReach(final Player player) {
		return player.getBlockReach();
	}

	@Override
	public boolean isFake(final Player player) {
		return player.getClass().getName().contains("FakePlayer") || player.getClass().getName().contains("fakeplayer");
	}

	@Override
	public <T extends Entity, P> EntityBuilder<T, P> loaderEntityTransform(final EntityBuilder<T, P> builder, final SimEntityTypes.EntityLoaderData data) {
		return builder.properties(p -> {
			if (data.immuneToFire()) {
				p.fireImmune();
			}
			p.clientTrackingRange(data.clientTrackingRange());
			p.updateInterval(data.updateFrequency());
			p.sized(data.width(), data.height());
			if (data.eyeHeight() != 0) {
				p.eyeHeight(data.eyeHeight());
			}
		});
	}
}
