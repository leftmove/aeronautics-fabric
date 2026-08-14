package foundry.veil.api.network;

import foundry.veil.api.network.handler.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

public interface VeilPacketChannel {
	<T extends CustomPacketPayload> void register(
			ResourceLocation id,
			StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
			VeilPacketManager.Handler<T> handler,
			boolean serverbound
	);

	void sendToServer(CustomPacketPayload payload);

	void sendToPlayer(ServerPlayer player, CustomPacketPayload payload);

	void sendTracking(Entity entity, CustomPacketPayload payload);

	void sendTracking(LevelChunk chunk, CustomPacketPayload payload);

	void sendAll(MinecraftServer server, CustomPacketPayload payload);

	@FunctionalInterface
	interface Handler<T extends CustomPacketPayload> {
		void handle(T payload, PacketContext context);
	}
}
