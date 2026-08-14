package foundry.veil.api.network;

import foundry.veil.api.network.handler.ClientPacketContext;
import foundry.veil.api.network.handler.PacketContext;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;

import java.util.function.BiConsumer;

public final class VeilPacketManager {
	private final VeilPacketChannel channel;

	private VeilPacketManager(final VeilPacketChannel channel) {
		this.channel = channel;
	}

	public static VeilPacketManager create(final String modId, final String version) {
		return new VeilPacketManager(VeilPacketBackend.INSTANCE.createChannel(modId, version));
	}

	public <T extends CustomPacketPayload> void registerServerbound(
			final CustomPacketPayload.Type<T> type,
			final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
			final BiConsumer<T, ServerPacketContext> handler
	) {
		PacketLookup.bind(type.id(), this);
		this.channel.register(type.id(), codec, (payload, context) -> handler.accept(payload, (ServerPacketContext) context), true);
	}

	public <T extends CustomPacketPayload> void registerClientbound(
			final CustomPacketPayload.Type<T> type,
			final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
			final BiConsumer<T, ClientPacketContext> handler
	) {
		PacketLookup.bind(type.id(), this);
		this.channel.register(type.id(), codec, (payload, context) -> handler.accept(payload, (ClientPacketContext) context), false);
	}

	public static PacketSink server() {
		return (packet, others) -> {
			send(packet);
			for (final CustomPacketPayload extra : others) {
				send(extra);
			}
		};
	}

	public static PacketSink player(final ServerPlayer player) {
		return (packet, others) -> {
			dispatch(player, packet);
			for (final CustomPacketPayload extra : others) {
				dispatch(player, extra);
			}
		};
	}

	public static PacketSink tracking(final Entity entity) {
		return (packet, others) -> {
			sendTracking(entity, packet);
			for (final CustomPacketPayload extra : others) {
				sendTracking(entity, extra);
			}
		};
	}

	public static PacketSink tracking(final BlockEntity blockEntity) {
		if (blockEntity.getLevel() instanceof final ServerLevel level) {
			return tracking(level, blockEntity.getBlockPos());
		}
		return (packet, others) -> {
		};
	}

	public static PacketSink tracking(final ServerLevel level, final BlockPos pos) {
		final LevelChunk chunk = level.getChunkAt(pos);
		return (packet, others) -> {
			sendTracking(chunk, packet);
			for (final CustomPacketPayload extra : others) {
				sendTracking(chunk, extra);
			}
		};
	}

	public static PacketSink all(final MinecraftServer server) {
		return (packet, others) -> {
			sendAll(server, packet);
			for (final CustomPacketPayload extra : others) {
				sendAll(server, extra);
			}
		};
	}

	public static void dispatch(final ServerPlayer player, final CustomPacketPayload payload) {
		manager(payload).channel.sendToPlayer(player, payload);
	}

	private static void send(final CustomPacketPayload payload) {
		manager(payload).channel.sendToServer(payload);
	}

	private static void sendTracking(final Entity entity, final CustomPacketPayload payload) {
		manager(payload).channel.sendTracking(entity, payload);
	}

	private static void sendTracking(final LevelChunk chunk, final CustomPacketPayload payload) {
		manager(payload).channel.sendTracking(chunk, payload);
	}

	private static void sendAll(final MinecraftServer server, final CustomPacketPayload payload) {
		manager(payload).channel.sendAll(server, payload);
	}

	private static VeilPacketManager manager(final CustomPacketPayload payload) {
		return PacketLookup.get(payload.type().id());
	}

	@FunctionalInterface
	public interface PacketSink {
		void sendPacket(CustomPacketPayload packet, CustomPacketPayload... others);
	}

	@FunctionalInterface
	public interface Handler<T extends CustomPacketPayload> {
		void handle(T payload, PacketContext context);
	}

	private static final class PacketLookup {
		private static final java.util.Map<net.minecraft.resources.ResourceLocation, VeilPacketManager> BY_TYPE = new java.util.concurrent.ConcurrentHashMap<>();

		private PacketLookup() {
		}

		static void bind(final net.minecraft.resources.ResourceLocation id, final VeilPacketManager manager) {
			BY_TYPE.put(id, manager);
		}

		static VeilPacketManager get(final net.minecraft.resources.ResourceLocation id) {
			final VeilPacketManager manager = BY_TYPE.get(id);
			if (manager == null) {
				throw new IllegalStateException("Unregistered payload " + id);
			}
			return manager;
		}
	}
}
