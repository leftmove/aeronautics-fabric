package foundry.veil.api.network.handler;

import net.minecraft.server.level.ServerPlayer;

public interface ServerPacketContext extends PacketContext {
    @Override
    ServerPlayer player();
}
