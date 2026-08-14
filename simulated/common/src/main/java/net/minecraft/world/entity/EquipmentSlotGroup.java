package net.minecraft.world.entity;

public enum EquipmentSlotGroup {
    MAINHAND(EquipmentSlot.MAINHAND),
    OFFHAND(EquipmentSlot.OFFHAND),
    HAND(EquipmentSlot.MAINHAND),
    FEET(EquipmentSlot.FEET),
    LEGS(EquipmentSlot.LEGS),
    CHEST(EquipmentSlot.CHEST),
    HEAD(EquipmentSlot.HEAD),
    ARMOR(EquipmentSlot.CHEST),
    ANY(EquipmentSlot.MAINHAND);

    private final EquipmentSlot slot;

    EquipmentSlotGroup(final EquipmentSlot slot) {
        this.slot = slot;
    }

    public EquipmentSlot slot() {
        return this.slot;
    }
}
