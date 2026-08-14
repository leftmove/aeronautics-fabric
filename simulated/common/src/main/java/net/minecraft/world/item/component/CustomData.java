package net.minecraft.world.item.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public record CustomData(CompoundTag tag) {
    public static final Codec<CustomData> CODEC = CompoundTag.CODEC.xmap(CustomData::new, CustomData::tag);

    public CompoundTag copyTag() {
        return this.tag.copy();
    }

    public static void set(final DataComponentType<CustomData> type, final ItemStack stack, final CompoundTag tag) {
        dev.simulated_team.simulated.compat.ItemComponents.set(stack, type, new CustomData(tag));
    }
}
