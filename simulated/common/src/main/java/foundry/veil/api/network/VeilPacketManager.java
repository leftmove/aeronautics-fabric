package foundry.veil.api.network;

import foundry.veil.api.network.handler.ClientPacketContext;
import foundry.veil.api.network.handler.PacketContext;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class VeilPacketManager {
    private static final Map<ResourceLocation, VeilPacketManager> BY_TYPE = new ConcurrentHashMap<>();

    private final SimpleChannel channel;
    private final Map<ResourceLocation, Registered<?>> packets = new ConcurrentHashMap<>();

    private VeilPacketManager(final String modId, final String version) {
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

    public static VeilPacketManager create(final String modId, final String version) {
        return new VeilPacketManager(modId, version);
    }

    public <T extends CustomPacketPayload> void registerServerbound(
            final CustomPacketPayload.Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final BiConsumer<T, ServerPacketContext> handler
    ) {
        this.register(type, codec, (payload, context) -> handler.accept(payload, (ServerPacketContext) context), true);
    }

    public <T extends CustomPacketPayload> void registerClientbound(
            final CustomPacketPayload.Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final BiConsumer<T, ClientPacketContext> handler
    ) {
        this.register(type, codec, (payload, context) -> handler.accept(payload, (ClientPacketContext) context), false);
    }

    @SuppressWarnings("unchecked")
    private <T extends CustomPacketPayload> void register(
            final CustomPacketPayload.Type<T> type,
            final StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            final Handler<T> handler,
            final boolean serverbound
    ) {
        this.packets.put(type.id(), new Registered<>((StreamCodec<? super RegistryFriendlyByteBuf, CustomPacketPayload>) codec, (Handler<CustomPacketPayload>) handler, serverbound));
        BY_TYPE.put(type.id(), this);
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
            sendAll(packet);
            for (final CustomPacketPayload extra : others) {
                sendAll(extra);
            }
        };
    }

    public static void dispatch(final ServerPlayer player, final CustomPacketPayload payload) {
        manager(payload).channel.send(PacketDistributor.PLAYER.with(() -> player), new PacketEnvelope(payload));
    }

    private static void send(final CustomPacketPayload payload) {
        manager(payload).channel.sendToServer(new PacketEnvelope(payload));
    }

    private static void sendTracking(final Entity entity, final CustomPacketPayload payload) {
        manager(payload).channel.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), new PacketEnvelope(payload));
    }

    private static void sendTracking(final LevelChunk chunk, final CustomPacketPayload payload) {
        manager(payload).channel.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), new PacketEnvelope(payload));
    }

    private static void sendAll(final CustomPacketPayload payload) {
        manager(payload).channel.send(PacketDistributor.ALL.noArg(), new PacketEnvelope(payload));
    }

    private static VeilPacketManager manager(final CustomPacketPayload payload) {
        final VeilPacketManager manager = BY_TYPE.get(payload.type().id());
        if (manager == null) {
            throw new IllegalStateException("Unregistered payload " + payload.type().id());
        }
        return manager;
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
            final Handler<CustomPacketPayload> handler = (Handler<CustomPacketPayload>) registered.handler;
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

    @FunctionalInterface
    public interface PacketSink {
        void sendPacket(CustomPacketPayload packet, CustomPacketPayload... others);
    }

    @FunctionalInterface
    public interface Handler<T extends CustomPacketPayload> {
        void handle(T payload, PacketContext context);
    }

    private record Registered<T extends CustomPacketPayload>(
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            Handler<T> handler,
            boolean serverbound
    ) {
    }

    public static final class PacketEnvelope {
        private final CustomPacketPayload payload;

        public PacketEnvelope(final CustomPacketPayload payload) {
            this.payload = payload;
        }
    }
}
