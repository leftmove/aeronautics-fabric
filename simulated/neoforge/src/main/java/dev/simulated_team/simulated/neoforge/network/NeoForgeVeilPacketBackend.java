package dev.simulated_team.simulated.neoforge.network;

import foundry.veil.api.network.VeilPacketBackend;
import foundry.veil.api.network.VeilPacketChannel;
import foundry.veil.api.network.VeilPacketManager;
import foundry.veil.api.network.handler.ClientPacketContext;
import foundry.veil.api.network.handler.PacketContext;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

public final class NeoForgeVeilPacketBackend implements VeilPacketBackend {
	@Override
	public VeilPacketChannel createChannel(final String modId, final String version) {
		return new ForgeVeilPacketChannel(modId, version);
	}

	private static final class ForgeVeilPacketChannel implements VeilPacketChannel {
		private final SimpleChannel channel;
		private final Map<ResourceLocation, Registered<?>> packets = new ConcurrentHashMap<>();

		private ForgeVeilPacketChannel(final String modId, final String version) {
			this.channel = NetworkRegistry.newSimpleChannel(
					new ResourceLocation(modId, "main"),
					() -> version,
					version::equals,
					version::equals
			);
			this.channel.registerMessage(
					0,
					PacketEnvelope.class,
					this::encode,
					this::decode,
					this::handle
			);
		}

		@Override
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
			this.channel.sendToServer(new PacketEnvelope(payload));
		}

		@Override
		public void sendToPlayer(final ServerPlayer player, final CustomPacketPayload payload) {
			this.channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketEnvelope(payload));
		}

		@Override
		public void sendTracking(final Entity entity, final CustomPacketPayload payload) {
			this.channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new PacketEnvelope(payload));
		}

		@Override
		public void sendTracking(final LevelChunk chunk, final CustomPacketPayload payload) {
			this.channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new PacketEnvelope(payload));
		}

		@Override
		public void sendAll(final MinecraftServer server, final CustomPacketPayload payload) {
			this.channel.send(PacketDistributor.ALL.noArg(), new PacketEnvelope(payload));
		}

		private void encode(final PacketEnvelope envelope, final FriendlyByteBuf buf) {
			buf.writeResourceLocation(envelope.payload.type().id());
			final Registered<?> registered = this.packets.get(envelope.payload.type().id());
			encode(registered.codec, new RegistryFriendlyByteBuf(buf), envelope.payload);
		}

		private PacketEnvelope decode(final FriendlyByteBuf buf) {
			final ResourceLocation id = buf.readResourceLocation();
			final Registered<?> registered = this.packets.get(id);
			final CustomPacketPayload payload = registered.codec.decode(new RegistryFriendlyByteBuf(buf));
			return new PacketEnvelope(payload);
		}

		private void handle(final PacketEnvelope envelope, final Supplier<NetworkEvent.Context> contextSupplier) {
			final NetworkEvent.Context context = contextSupplier.get();
			final Registered<?> registered = this.packets.get(envelope.payload.type().id());
			context.enqueueWork(() -> {
				@SuppressWarnings("unchecked")
				final VeilPacketManager.Handler<CustomPacketPayload> handler = (VeilPacketManager.Handler<CustomPacketPayload>) registered.handler;
				if (registered.serverbound) {
					final ServerPlayer player = context.getSender();
					if (player != null) {
						handler.handle(envelope.payload, (ServerPacketContext) () -> player);
					}
				} else {
					DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
						final var minecraft = Minecraft.getInstance();
						if (minecraft.player != null) {
							handler.handle(envelope.payload, (ClientPacketContext) () -> minecraft.player);
						}
					});
				}
			});
			context.setPacketHandled(true);
		}

		@SuppressWarnings("unchecked")
		private static <T extends CustomPacketPayload> void encode(
				final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
				final RegistryFriendlyByteBuf buf,
				final CustomPacketPayload payload
		) {
			codec.encode(buf, (T) payload);
		}

		private record Registered<T extends CustomPacketPayload>(
				StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
				VeilPacketManager.Handler<T> handler,
				boolean serverbound
		) {
		}

		private static final class PacketEnvelope {
			private final CustomPacketPayload payload;

			private PacketEnvelope(final CustomPacketPayload payload) {
				this.payload = payload;
			}
		}
	}
}
