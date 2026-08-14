package dev.simulated_team.simulated.content.navigation_targets;

import dev.simulated_team.simulated.content.blocks.nav_table.NavTableBlockEntity;
import dev.simulated_team.simulated.content.blocks.nav_table.navigation_target.NavigationTarget;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapBanner;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public class MapNavigationTarget implements NavigationTarget {
	@Override
	public @Nullable Vec3 getTarget(final NavTableBlockEntity navBE, final ItemStack self) {
		final Level level = navBE.getLevel();
		final Vec3 pos = navBE.getProjectedSelfPos();
		return getNearestDecorationPos(level, pos, self);
	}

	private static Vec3 getNearestDecorationPos(final Level level, final Vec3 pos, final ItemStack stack) {
		final MapItemSavedData mapData = MapItem.getSavedData(stack, level);
		if (mapData == null) {
			return null;
		}

		double closestDist = Double.POSITIVE_INFINITY;
		Vec3 closestPos = null;
		for (final MapDecoration decoration : mapData.getDecorations()) {
			if (decoration.getType() == MapDecoration.Type.PLAYER
					|| decoration.getType() == MapDecoration.Type.PLAYER_OFF_MAP
					|| decoration.getType() == MapDecoration.Type.PLAYER_OFF_LIMITS) {
				continue;
			}
			final double worldX = mapData.centerX + decoration.getX() / 2.0 * (1 << mapData.scale);
			final double worldZ = mapData.centerZ + decoration.getY() / 2.0 * (1 << mapData.scale);
			final double dist = pos.distanceToSqr(worldX, pos.y, worldZ);
			if (dist < closestDist) {
				closestPos = new Vec3(worldX, pos.y, worldZ);
				closestDist = dist;
			}
		}

		final Collection<MapBanner> banners = mapData.getBanners();
		for (final MapBanner banner : banners) {
			final Vec3 bannerPos = Vec3.atCenterOf(banner.getPos());
			final double dist = pos.distanceToSqr(bannerPos.x, pos.y, bannerPos.z);
			if (dist < closestDist) {
				closestPos = bannerPos;
				closestDist = dist;
			}
		}

		return closestPos;
	}
}
