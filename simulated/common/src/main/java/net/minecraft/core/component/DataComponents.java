package net.minecraft.core.component;

import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.LodestoneTracker;
import net.minecraft.world.item.component.MapDecorations;
import net.minecraft.world.level.saveddata.maps.MapId;

public final class DataComponents {
    public static final DataComponentType<Component> CUSTOM_NAME = DataComponentType.<Component>builder()
            .persistent(net.minecraft.util.ExtraCodecs.COMPONENT)
            .build();
    public static final DataComponentType<CustomData> BLOCK_ENTITY_DATA = DataComponentType.<CustomData>builder()
            .persistent(CustomData.CODEC)
            .build();
    public static final DataComponentType<Integer> MAX_DAMAGE = DataComponentType.<Integer>builder()
            .persistent(com.mojang.serialization.Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
            .build();
    public static final DataComponentType<ItemAttributeModifiers> ATTRIBUTE_MODIFIERS = DataComponentType.<ItemAttributeModifiers>builder()
            .build();
    public static final DataComponentType<LodestoneTracker> LODESTONE_TRACKER = DataComponentType.<LodestoneTracker>builder()
            .build();
    public static final DataComponentType<MapDecorations> MAP_DECORATIONS = DataComponentType.<MapDecorations>builder()
            .build();
    public static final DataComponentType<MapId> MAP_ID = DataComponentType.<MapId>builder()
            .persistent(com.mojang.serialization.Codec.INT.xmap(MapId::new, MapId::id))
            .build();

    static {
        CUSTOM_NAME.bind(new net.minecraft.resources.ResourceLocation("minecraft", "custom_name"));
        BLOCK_ENTITY_DATA.bind(new net.minecraft.resources.ResourceLocation("minecraft", "block_entity_data"));
        MAX_DAMAGE.bind(new net.minecraft.resources.ResourceLocation("minecraft", "max_damage"));
        ATTRIBUTE_MODIFIERS.bind(new net.minecraft.resources.ResourceLocation("minecraft", "attribute_modifiers"));
        LODESTONE_TRACKER.bind(new net.minecraft.resources.ResourceLocation("minecraft", "lodestone_tracker"));
        MAP_DECORATIONS.bind(new net.minecraft.resources.ResourceLocation("minecraft", "map_decorations"));
        MAP_ID.bind(new net.minecraft.resources.ResourceLocation("minecraft", "map_id"));
    }

    private DataComponents() {
    }
}
