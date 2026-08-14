package net.createmod.catnip.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public final class RegisteredObjectsHelper {
    private RegisteredObjectsHelper() {
    }

    public static ResourceLocation getKeyOrThrow(final ItemLike itemLike) {
        return getKeyOrThrow(itemLike.asItem());
    }

    public static ResourceLocation getKeyOrThrow(final Item item) {
        final ResourceLocation id = BuiltInRegistries.ITEM.getKey(item);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered item: " + item);
        }
        return id;
    }

    public static ResourceLocation getKeyOrThrow(final Block block) {
        final ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered block: " + block);
        }
        return id;
    }

    public static ResourceLocation getKeyOrThrow(final Fluid fluid) {
        final ResourceLocation id = BuiltInRegistries.FLUID.getKey(fluid);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered fluid: " + fluid);
        }
        return id;
    }

    public static ResourceLocation getKeyOrThrow(final BlockEntityType<?> type) {
        final ResourceLocation id = BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(type);
        if (id == null) {
            throw new IllegalArgumentException("Unregistered block entity type: " + type);
        }
        return id;
    }
}
