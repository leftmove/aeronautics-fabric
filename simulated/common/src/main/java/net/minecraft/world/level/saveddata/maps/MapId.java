package net.minecraft.world.level.saveddata.maps;

public record MapId(int id) {
    public String key() {
        return "map_" + this.id;
    }
}
