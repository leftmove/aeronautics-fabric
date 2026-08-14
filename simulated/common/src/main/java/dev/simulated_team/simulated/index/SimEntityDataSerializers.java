package dev.simulated_team.simulated.index;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.phys.Vec3;

public class SimEntityDataSerializers {

    public static final EntityDataSerializer<Vec3> VEC3 = EntityDataSerializer.simple(
            (buf, value) -> {
                buf.writeDouble(value.x);
                buf.writeDouble(value.y);
                buf.writeDouble(value.z);
            },
            buf -> new Vec3(buf.readDouble(), buf.readDouble(), buf.readDouble())
    );

    public static void register() {
        dev.simulated_team.simulated.service.SimEntityDataSerialization.INSTANCE.registerDataSerializer("vec3", VEC3);
    }
}
