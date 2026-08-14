package dev.eriksonn.aeronautics.index;

import com.simibubi.create.api.registry.CreateBuiltInRegistries;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointType;
import dev.eriksonn.aeronautics.Aeronautics;
import dev.eriksonn.aeronautics.service.AeroArmService;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AeroArmInteractionPoints {
    private static <T extends ArmInteractionPointType> void register(final String name, final T type) {
        Registry.register(CreateBuiltInRegistries.ARM_INTERACTION_POINT_TYPE, Aeronautics.path(name), type);
    }

    static {
        register("mounted_potato_cannon_point", new MountedPotatoCannonType());
    }

    public static class MountedPotatoCannonType extends ArmInteractionPointType {

        @Override
        public boolean canCreatePoint(final Level var1, final BlockPos var2, final BlockState var3) {
            return AeroBlocks.MOUNTED_POTATO_CANNON.has(var1.getBlockState(var2));
        }

        @Override
        public @Nullable ArmInteractionPoint createPoint(final Level var1, final BlockPos var2, final BlockState var3) {
            return AeroArmService.INSTANCE.createMountedPotatoCannonPoint(this, var1, var2, var3);
        }
    }

    public static void init() {

    }
}