package foundry.veil.api.network.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public interface ClientPacketContext extends PacketContext {
    @Override
    LocalPlayer player();

    default Minecraft client() {
        return Minecraft.getInstance();
    }
}
