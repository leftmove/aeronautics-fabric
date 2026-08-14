package foundry.veil.api.network.handler;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface PacketContext {
    Player player();

    default Level level() {
        return this.player().level();
    }

    default void disconnect(net.minecraft.network.chat.Component reason) {
        if (this.player() instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            serverPlayer.connection.disconnect(reason);
        }
    }
}
