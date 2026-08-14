package dev.simulated_team.simulated.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.AllArmInteractionPointTypes;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.service.SimArmService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class SimArmInteractions {
    private static <T extends ArmInteractionPointType> void register(final String name, final T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, Simulated.path(name), type);
    }

    static {
        register("portable_engine", new PortableEngineType());
        register("navigation_table", new NavTableType());
    }

    public static class PortableEngineType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(final Level level, final BlockPos pos, final BlockState state) {
            return SimBlocks.PORTABLE_ENGINES.contains(state.getBlock());
        }

        @Override
        public @Nullable ArmInteractionPoint createPoint(final Level level, final BlockPos pos, final BlockState state) {
            return SimArmService.INSTANCE.createPortableEnginePoint(this, level, pos, state);
        }
    }

    public static class NavTableType extends ArmInteractionPointType {
        @Override
        public boolean canCreatePoint(final Level level, final BlockPos pos, final BlockState state) {
            return SimBlocks.NAVIGATION_TABLE.has(state);
        }

        @Override
        public @Nullable ArmInteractionPoint createPoint(final Level level, final BlockPos pos, final BlockState state) {
            return new NavTablePoint(this, level, pos, state);
        }
    }

    public static class NavTablePoint extends AllArmInteractionPointTypes.DepotPoint {
        public NavTablePoint(final ArmInteractionPointType type, final Level level, final BlockPos pos, final BlockState state) {
            super(type, level, pos, state);
        }
    }

    public static void init() {}
}
