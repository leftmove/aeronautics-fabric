package dev.ryanhcode.offroad.events;

import com.simibubi.create.AllBlocks;
import dev.ryanhcode.offroad.content.blocks.wheel_mount.WheelMountBlockEntity;
import dev.ryanhcode.offroad.content.components.TireLike;
import dev.ryanhcode.offroad.handlers.client.MultiMiningClientHandler;
import dev.ryanhcode.offroad.handlers.server.MultiMiningServerManager;
import dev.ryanhcode.offroad.index.OffroadDataComponents;
import dev.ryanhcode.sable.sublevel.system.SubLevelPhysicsSystem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import dev.simulated_team.simulated.compat.ItemComponents;

public class OffroadCommonEvents {

    public static void modifyDefaultComponents() {
        ItemComponents.setDefault(AllBlocks.FLYWHEEL.get(), OffroadDataComponents.TIRE, TireLike.FLYWHEEL);
        ItemComponents.setDefault(AllBlocks.LARGE_WATER_WHEEL.get(), OffroadDataComponents.TIRE, TireLike.LARGE_WATER_WHEEL);
        ItemComponents.setDefault(AllBlocks.CRUSHING_WHEEL.get(), OffroadDataComponents.TIRE, TireLike.CRUSHING_WHEEL);
        ItemComponents.setDefault(AllBlocks.WATER_WHEEL.get(), OffroadDataComponents.TIRE, TireLike.WATER_WHEEL);
        ItemComponents.setDefault(AllBlocks.MECHANICAL_ROLLER.get(), OffroadDataComponents.TIRE, TireLike.MECHANICAL_ROLLER);
    }

    public static void physicsTick(final SubLevelPhysicsSystem physicsSystem, final double timeStep) {
        final ServerLevel level = physicsSystem.getLevel();
        WheelMountBlockEntity.applyAllBatchedForces(level, timeStep);
    }

    public static void tickLevelEvent(final Level level) {
        if (!level.isClientSide) {
            MultiMiningServerManager.tick(level);
        } else {
            MultiMiningClientHandler.tick(level);
        }
    }
}
