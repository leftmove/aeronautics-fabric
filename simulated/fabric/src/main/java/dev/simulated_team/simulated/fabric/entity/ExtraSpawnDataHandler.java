package dev.simulated_team.simulated.fabric.entity;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.EntityTrackingEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.entity.ExtraSpawnData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ExtraSpawnDataHandler {
	public static final ResourceLocation ID = Simulated.path("extra_spawn_data");
	private static final Map<Integer, byte[]> PENDING = new ConcurrentHashMap<>();

	private ExtraSpawnDataHandler() {
	}

	public static void register() {
		EntityTrackingEvents.START_TRACKING.register(ExtraSpawnDataHandler::onStartTracking);
	}

	public static void registerClient() {
		ClientPlayNetworking.registerGlobalReceiver(ID, (client, handler, buf, responseSender) -> {
			final int entityId = buf.readVarInt();
			final byte[] data = buf.readByteArray();
			client.execute(() -> {
				final var level = Minecraft.getInstance().level;
				if (level == null) {
					PENDING.put(entityId, data);
					return;
				}
				final Entity entity = level.getEntity(entityId);
				if (entity == null) {
					PENDING.put(entityId, data);
					return;
				}
				apply(entity, data);
			});
		});
		ClientEntityEvents.ENTITY_LOAD.register((entity, world) -> applyPending(entity));
	}

	private static void onStartTracking(final Entity entity, final ServerPlayer player) {
		if (!(entity instanceof final ExtraSpawnData extra)) {
			return;
		}

		final RegistryFriendlyByteBuf dataBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), entity.level().registryAccess());
		extra.writeSpawnData(dataBuf);
		final byte[] data = new byte[dataBuf.readableBytes()];
		dataBuf.readBytes(data);

		final FriendlyByteBuf buf = PacketByteBufs.create();
		buf.writeVarInt(entity.getId());
		buf.writeByteArray(data);
		ServerPlayNetworking.send(player, ID, buf);
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
		final RegistryFriendlyByteBuf buf = new RegistryFriendlyByteBuf(Unpooled.wrappedBuffer(data), entity.level().registryAccess());
		extra.readSpawnData(buf);
	}
}
