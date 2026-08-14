package dev.simulated_team.simulated.multiloader.inventory;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

public interface NBTSerializable {
    public CompoundTag write();

    void read(CompoundTag nbt);
}

