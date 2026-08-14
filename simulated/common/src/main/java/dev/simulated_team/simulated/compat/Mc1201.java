package dev.simulated_team.simulated.compat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

public final class Mc1201 {
    private Mc1201() {
    }

    public static AABB encapsulatingFullBlocks(final BlockPos a, final BlockPos b) {
        final int minX = Math.min(a.getX(), b.getX());
        final int minY = Math.min(a.getY(), b.getY());
        final int minZ = Math.min(a.getZ(), b.getZ());
        final int maxX = Math.max(a.getX(), b.getX()) + 1;
        final int maxY = Math.max(a.getY(), b.getY()) + 1;
        final int maxZ = Math.max(a.getZ(), b.getZ()) + 1;
        return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
