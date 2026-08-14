package dev.simulated_team.simulated.fabric.network;

import foundry.veil.api.network.VeilPacketBackend;
import foundry.veil.api.network.VeilPacketChannel;
import foundry.veil.api.network.VeilPacketManager;
import foundry.veil.api.network.handler.ClientPacketContext;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class FabricVeilPacketBackend implements VeilPacketBackend {
	@Override
	public VeilPacketChannel createChannel(final String modId, final String version) {
		return new FabricVeilPacketChannel(modId);
	}

	static final class FabricVeilPacketChannel implements VeilPacketChannel {
		private final ResourceLocation channelId;
		private final Map<ResourceLocation, Registered<?>> packets = new ConcurrentHashMap<>();

		private FabricVeilPacketChannel(final String modId) {
			this.channelId = new ResourceLocation(modId, "main");
			ServerPlayNetworking.registerGlobalReceiver(this.channelId, (server, player, networkHandler, buf, responseSender) -> {
				final PacketEnvelope envelope = this.decode(buf);
				final Registered<?> registered = this.packets.get(envelope.payload.type().id());
				if (registered == null || !registered.serverbound) {
					return;
				}
				@SuppressWarnings("unchecked")
				final VeilPacketManager.Handler<CustomPacketPayload> packetHandler = (VeilPacketManager.Handler<CustomPacketPayload>) registered.handler;
				server.execute(() -> packetHandler.handle(envelope.payload, (ServerPacketContext) () -> player));
			});
			if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
				ClientReceiver.register(this);
			}
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T extends CustomPacketPayload> void register(
				final ResourceLocation id,
				final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
				final VeilPacketManager.Handler<T> handler,
				final boolean serverbound
		) {
			this.packets.put(id, new Registered<>((StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayload>) codec, (VeilPacketManager.Handler<CustomPacketPayload>) handler, serverbound));
		}

		@Override
		public void sendToServer(final CustomPacketPayload payload) {
			ClientSender.send(this.channelId, this.encode(payload));
		}

		@Override
		public void sendToPlayer(final ServerPlayer player, final CustomPacketPayload payload) {
			ServerPlayNetworking.send(player, this.channelId, this.encode(payload));
		}

		@Override
		public void sendTracking(final Entity entity, final CustomPacketPayload payload) {
			for (final ServerPlayer player : PlayerLookup.tracking(entity)) {
				this.sendToPlayer(player, payload);
			}
		}

		@Override
		public void sendTracking(final LevelChunk chunk, final CustomPacketPayload payload) {
			for (final ServerPlayer player : PlayerLookup.tracking((ServerLevel) chunk.getLevel(), chunk.getPos())) {
				this.sendToPlayer(player, payload);
			}
		}

		@Override
		public void sendAll(final MinecraftServer server, final CustomPacketPayload payload) {
			for (final ServerPlayer player : PlayerLookup.all(server)) {
				this.sendToPlayer(player, payload);
			}
		}

		private FriendlyByteBuf encode(final CustomPacketPayload payload) {
			final FriendlyByteBuf buf = PacketByteBufs.create();
			buf.writeResourceLocation(payload.type().id());
			final Registered<?> registered = this.packets.get(payload.type().id());
			encode(registered.codec, new RegistryFriendlyByteBuf(buf), payload);
			return buf;
		}

		PacketEnvelope decode(final FriendlyByteBuf buf) {
			final ResourceLocation id = buf.readResourceLocation();
			final Registered<?> registered = this.packets.get(id);
			final CustomPacketPayload payload = registered.codec.decode(new RegistryFriendlyByteBuf(buf));
			return new PacketEnvelope(payload);
		}

		Registered<?> registered(final ResourceLocation id) {
			return this.packets.get(id);
		}

		@SuppressWarnings("unchecked")
		private static <T extends CustomPacketPayload> void encode(
				final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
				final RegistryFriendlyByteBuf buf,
				final CustomPacketPayload payload
		) {
			codec.encode(buf, (T) payload);
		}

		record Registered<T extends CustomPacketPayload>(
				StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
				VeilPacketManager.Handler<T> handler,
				boolean serverbound
		) {
		}

		record PacketEnvelope(CustomPacketPayload payload) {
		}

		private static final class ClientSender {
			private static void send(final ResourceLocation channelId, final FriendlyByteBuf buf) {
				ClientPlayNetworking.send(channelId, buf);
			}
		}

		private static final class ClientReceiver {
			private static void register(final FabricVeilPacketChannel channel) {
				ClientPlayNetworking.registerGlobalReceiver(channel.channelId, (client, networkHandler, buf, responseSender) -> {
					final PacketEnvelope envelope = channel.decode(buf);
					final Registered<?> registered = channel.packets.get(envelope.payload.type().id());
					if (registered == null || registered.serverbound) {
						return;
					}
					@SuppressWarnings("unchecked")
					final VeilPacketManager.Handler<CustomPacketPayload> packetHandler = (VeilPacketManager.Handler<CustomPacketPayload>) registered.handler;
					client.execute(() -> {
						if (Minecraft.getInstance().player != null) {
							packetHandler.handle(envelope.payload, (ClientPacketContext) () -> Minecraft.getInstance().player);
						}
					});
				});
			}
		}
	}
}
