package net.minecraft.world.item.component;

import net.minecraft.world.level.saveddata.maps.MapDecoration;

import java.util.Map;

public record MapDecorations(Map<String, Entry> decorations) {
    public record Entry(MapDecoration.Type type, double x, double z, float rot) {
    }
}
