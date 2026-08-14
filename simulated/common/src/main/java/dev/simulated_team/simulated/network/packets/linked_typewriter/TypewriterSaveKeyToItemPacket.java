package dev.simulated_team.simulated.network.packets.linked_typewriter;

import com.mojang.serialization.DataResult;
import dev.simulated_team.simulated.Simulated;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterEntries;
import dev.simulated_team.simulated.index.SimBlocks;
import foundry.veil.api.network.handler.ServerPacketContext;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import dev.simulated_team.simulated.compat.ItemComponents;

public record TypewriterSaveKeyToItemPacket(InteractionHand hand, LinkedTypewriterEntries.KeyboardEntry entry) implements CustomPacketPayload {

    public static Type<TypewriterSaveKeyToItemPacket> TYPE = new Type<>(Simulated.path("linked_typewriter_bind_item"));

    public static StreamCodec<RegistryFriendlyByteBuf, TypewriterSaveKeyToItemPacket> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, (packet) -> packet.hand.ordinal(),
            LinkedTypewriterEntries.KeyboardEntry.STREAM_CODEC, TypewriterSaveKeyToItemPacket::entry,
            (h, e) -> new TypewriterSaveKeyToItemPacket(InteractionHand.values()[h], e));

    public void handle(final ServerPacketContext context) {
        final ServerPlayer player = context.player();
        final ItemStack item = player.getItemInHand(this.hand);

        CompoundTag currentTag = new CompoundTag();
        if (ItemComponents.has(item, DataComponents.BLOCK_ENTITY_DATA)) {
            currentTag = ItemComponents.get(item, DataComponents.BLOCK_ENTITY_DATA).copyTag();
        } else {
            currentTag.putString("id", item.getItem().toString());
        }

        final RegistryOps<Tag> ops = RegistryOps.create(NbtOps.INSTANCE, context.level().registryAccess());
        final DataResult<Tag> result = LinkedTypewriterEntries.KeyboardEntry.CODEC.encodeStart(ops, this.entry);
        if (result.error().isPresent()) {
            Simulated.LOGGER.warn("Unable to process entry for item saving!: {}", result.error().get().message());
            return;
        }

        final CompoundTag entryTag = (CompoundTag) result.getOrThrow(false, s -> {});
        if (!currentTag.contains("Keys")) {
            currentTag.put("Keys", new ListTag());
        }

        final ListTag keys = currentTag.getList("Keys", Tag.TAG_COMPOUND);
        boolean alreadyPresent = false;

        for (int i = 0; i < keys.size(); i++) {
            final Tag key = keys.get(i);
            final int glfwKey = ((CompoundTag) key).getInt("GLFWKey");

            if (glfwKey == this.entry.glfwKeyCode) {
                alreadyPresent = true;
                keys.set(i, entryTag);
                break;
            }
        }

        if (!alreadyPresent) {
            keys.add(entryTag);
        }

        currentTag.put("Keys", keys);
        if (item.is(SimBlocks.LINKED_TYPEWRITER.get().asItem())) {
            CustomData.set(DataComponents.BLOCK_ENTITY_DATA, item, currentTag);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
