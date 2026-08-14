package net.minecraft.world.item.component;

import net.minecraft.core.GlobalPos;

import java.util.Optional;

public record LodestoneTracker(Optional<GlobalPos> target, boolean tracked) {
}
