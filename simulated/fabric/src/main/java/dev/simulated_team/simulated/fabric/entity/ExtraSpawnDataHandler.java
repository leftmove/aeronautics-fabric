package dev.simulated_team.simulated.fabric.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import dev.simulated_team.simulated.entity.ExtraSpawnData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtraSpawnDataHandler {
	private static final Map<Integer, byte[]> PENDING = new ConcurrentHashMap<>();

	private ExtraSpawnDataHandler() {
	}

	public static void register() {
		PayloadTypeRegistry.playS2C().register(ExtraSpawnDataPayload.TYPE, ExtraSpawnDataPayload.STREAM_CODEC);
		EntityTrackingEvents.START_TRACKING.register(ExtraSpawnDataHandler::onStartTracking);
	}

	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(ExtraSpawnDataPayload.TYPE, ExtraSpawnDataHandler::handleClient);
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> applyPending(entity));
	}

	private static void onStartTracking(final Entity entity, final Player player) {
		if (!(entity instanceof final ExtraSpawnData extra) || !(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)) {
			return;
		}

		final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), entity.registryAccess());
		extra.writeSpawnData(buf);
		final byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		ServerPlayNetworking.send(serverPlayer, new ExtraSpawnDataPayload(entity.getId(), data));
	}

	private static void handleClient(final ExtraSpawnDataPayload payload, final ClientPlayNetworking.Context context) {
		context.client().execute(() -> {
			final var level = Minecraft.getInstance().level;
			if (level == null) {
				PENDING.put(payload.entityId(), payload.data());
				return;
			}
			final Entity entity = level.getEntity(payload.entityId());
			if (entity == null) {
				PENDING.put(payload.entityId(), payload.data());
				return;
			}
			apply(entity, payload.data());
		});
	}

	private static void applyPending(final Entity entity) {
		final byte[] data = PENDING.remove(entity.getId());
		if (data != null) {
			apply(entity, data);
		}
	}

	private static void apply(final Entity entity, final byte[] data) {
		if (!(entity instanceof final ExtraSpawnData extra)) {
			return;
		}
		final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), entity.registryAccess());
		extra.readSpawnData(buf);
	}

	public record ExtraSpawnDataPayload(int entityId, byte[] data) implements CustomPacketPayload {
		public static final CustomPacketPayload.Type<ExtraSpawnDataPayload> TYPE =
				new CustomPacketPayload.Type<>(dev.simulated_team.simulated.Simulated.path("extra_spawn_data"));

		public static final StreamCodec<RegistryFriendlyByteBuf, ExtraSpawnDataPayload> STREAM_CODEC = StreamCodec.composite(
				ByteBufCodecs.VAR_INT, ExtraSpawnDataPayload::entityId,
				ByteBufCodecs.BYTE_ARRAY, ExtraSpawnDataPayload::data,
				ExtraSpawnDataPayload::new
		);

		@Override
		public Type<? extends CustomPacketPayload> type() {
			return TYPE;
		}
	}
}
