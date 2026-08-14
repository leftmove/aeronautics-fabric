package dev.simulated_team.simulated.compat;

import com.mojang.serialization.DataResult;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ItemLike;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class ItemComponents {
    private static final String TAG_KEY = "SimulatedComponents";
    private static final Map<Item.Properties, List<Pending<?>>> PENDING = new IdentityHashMap<>();
    private static final Map<Item, Map<DataComponentType<?>, Object>> DEFAULTS = new IdentityHashMap<>();

    private ItemComponents() {
    }

    public static <T> Item.Properties with(final Item.Properties properties, final DataComponentType<T> type, final T value) {
        PENDING.computeIfAbsent(properties, key -> new ArrayList<>()).add(new Pending<>(type, value));
        return properties;
    }

    public static void applyPending(final Item item, final Item.Properties properties) {
        final List<Pending<?>> pending = PENDING.remove(properties);
        if (pending == null) {
            return;
        }
        for (final Pending<?> entry : pending) {
            putDefault(item, entry);
        }
    }

    public static <T> void setDefault(final ItemLike item, final DataComponentType<T> type, final T value) {
        DEFAULTS.computeIfAbsent(item.asItem(), key -> new IdentityHashMap<>()).put(type, value);
    }

    public static <T> T get(final ItemStack stack, final DataComponentType<T> type) {
        if (type == DataComponents.CUSTOM_NAME) {
            return stack.hasCustomHoverName() ? (T) stack.getHoverName() : null;
        }
        if (type == DataComponents.BLOCK_ENTITY_DATA) {
            final CompoundTag tag = BlockItem.getBlockEntityData(stack);
            return tag == null ? null : (T) new CustomData(tag);
        }
        if (type == DataComponents.MAX_DAMAGE) {
            return stack.isDamageableItem() ? (T) Integer.valueOf(stack.getMaxDamage()) : null;
        }
        final T stored = read(stack, type);
        if (stored != null) {
            return stored;
        }
        return defaultValue(stack.getItem(), type);
    }

    public static boolean has(final ItemStack stack, final DataComponentType<?> type) {
        return get(stack, type) != null;
    }

    public static <T> T set(final ItemStack stack, final DataComponentType<T> type, final T value) {
        final T previous = get(stack, type);
        if (type == DataComponents.CUSTOM_NAME) {
            stack.setHoverName((Component) value);
            return previous;
        }
        if (type == DataComponents.BLOCK_ENTITY_DATA && value instanceof final CustomData data) {
            stack.getOrCreateTag().put(BlockItem.BLOCK_ENTITY_TAG, data.copyTag());
            return previous;
        }
        write(stack, type, value);
        return previous;
    }

    public static <T> T remove(final ItemStack stack, final DataComponentType<T> type) {
        final T previous = get(stack, type);
        if (type == DataComponents.CUSTOM_NAME) {
            stack.resetHoverName();
            return previous;
        }
        if (type == DataComponents.BLOCK_ENTITY_DATA) {
            if (stack.getTag() != null) {
                stack.getTag().remove(BlockItem.BLOCK_ENTITY_TAG);
            }
            return previous;
        }
        final CompoundTag tag = stack.getTagElement(TAG_KEY);
        if (tag != null && type.getId() != null) {
            tag.remove(type.getId().toString());
            if (tag.isEmpty()) {
                stack.removeTagKey(TAG_KEY);
            }
        }
        return previous;
    }

    public static DataComponentMap view(final ItemStack stack) {
        final DataComponentMap.Builder builder = DataComponentMap.builder();
        final Map<DataComponentType<?>, Object> defaults = DEFAULTS.get(stack.getItem());
        if (defaults != null) {
            defaults.forEach((type, value) -> put(builder, type, value));
        }
        final CompoundTag tag = stack.getTagElement(TAG_KEY);
        if (tag != null) {
            for (final String key : tag.getAllKeys()) {
                // values are read through typed getters; map view is best-effort
            }
        }
        if (stack.hasCustomHoverName()) {
            builder.set(DataComponents.CUSTOM_NAME, stack.getHoverName());
        }
        if (stack.isDamageableItem()) {
            builder.set(DataComponents.MAX_DAMAGE, stack.getMaxDamage());
        }
        return builder.build();
    }

    public static DataComponentPatch patchOf(final ItemStack stack) {
        final DataComponentPatch.Builder builder = DataComponentPatch.builder();
        final CompoundTag tag = stack.getTagElement(TAG_KEY);
        if (tag != null) {
            for (final DataComponentType<?> type : knownTypes(stack)) {
                final Object value = get(stack, type);
                if (value != null) {
                    putPatch(builder, type, value);
                }
            }
        }
        return builder.build();
    }

    public static void apply(final ItemStack stack, final DataComponentPatch patch) {
        for (final var entry : patch.entrySet()) {
            if (entry.getValue().isEmpty()) {
                remove(stack, entry.getKey());
            } else {
                setUnchecked(stack, entry.getKey(), entry.getValue().get());
            }
        }
    }

    public static void apply(final ItemStack stack, final DataComponentMap map) {
        // best-effort: custom name / max damage already live on the stack
    }

    @SuppressWarnings("unchecked")
    private static <T> T read(final ItemStack stack, final DataComponentType<T> type) {
        if (type.codec() == null || type.getId() == null) {
            return null;
        }
        final CompoundTag tag = stack.getTagElement(TAG_KEY);
        if (tag == null || !tag.contains(type.getId().toString())) {
            return null;
        }
        final Tag encoded = tag.get(type.getId().toString());
        final DataResult<T> result = type.codec().parse(NbtOps.INSTANCE, encoded);
        return result.result().orElse(null);
    }

    private static <T> void write(final ItemStack stack, final DataComponentType<T> type, final T value) {
        if (type.codec() == null || type.getId() == null) {
            return;
        }
        if (value == null) {
            remove(stack, type);
            return;
        }
        type.codec().encodeStart(NbtOps.INSTANCE, value).result().ifPresent(encoded ->
                stack.getOrCreateTagElement(TAG_KEY).put(type.getId().toString(), encoded));
    }

    @SuppressWarnings("unchecked")
    private static <T> T defaultValue(final Item item, final DataComponentType<T> type) {
        final Map<DataComponentType<?>, Object> defaults = DEFAULTS.get(item);
        return defaults == null ? null : (T) defaults.get(type);
    }

    @SuppressWarnings("unchecked")
    private static <T> void putDefault(final Item item, final Pending<?> pending) {
        DEFAULTS.computeIfAbsent(item, key -> new IdentityHashMap<>()).put(pending.type, pending.value);
    }

    @SuppressWarnings("unchecked")
    private static <T> void put(final DataComponentMap.Builder builder, final DataComponentType<?> type, final Object value) {
        builder.set((DataComponentType<T>) type, (T) value);
    }

    @SuppressWarnings("unchecked")
    private static <T> void putPatch(final DataComponentPatch.Builder builder, final DataComponentType<?> type, final Object value) {
        builder.set((DataComponentType<T>) type, (T) value);
    }

    @SuppressWarnings("unchecked")
    private static <T> void setUnchecked(final ItemStack stack, final DataComponentType<?> type, final Object value) {
        set(stack, (DataComponentType<T>) type, (T) value);
    }

    private static List<DataComponentType<?>> knownTypes(final ItemStack stack) {
        final List<DataComponentType<?>> types = new ArrayList<>();
        final Map<DataComponentType<?>, Object> defaults = DEFAULTS.get(stack.getItem());
        if (defaults != null) {
            types.addAll(defaults.keySet());
        }
        return types;
    }

    private record Pending<T>(DataComponentType<T> type, T value) {
    }
}
