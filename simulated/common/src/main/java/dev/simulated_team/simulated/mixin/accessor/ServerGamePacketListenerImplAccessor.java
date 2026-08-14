package dev.simulated_team.simulated.mixin.accessor;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ServerGamePacketListenerImpl.class)
public interface ServerGamePacketListenerImplAccessor {
    @Accessor("aboveGroundTickCount")
    void simulated$setAboveGroundTickCount(int ticks);

    @Accessor("aboveGroundVehicleTickCount")
    void simulated$setAboveGroundVehicleTickCount(int ticks);
}
