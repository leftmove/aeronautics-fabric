package net.minecraft.world.item.component;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.List;

public record ItemAttributeModifiers(List<Entry> entries) {
    public static Builder builder() {
        return new Builder();
    }

    public record Entry(Holder<Attribute> attribute, AttributeModifier modifier, EquipmentSlot slot) {
    }

    public static final class Builder {
        private final List<Entry> entries = new ArrayList<>();

        public Builder add(final Holder<Attribute> attribute, final AttributeModifier modifier, final net.minecraft.world.entity.EquipmentSlotGroup group) {
            this.entries.add(new Entry(attribute, modifier, group.slot()));
            return this;
        }

        public ItemAttributeModifiers build() {
            return new ItemAttributeModifiers(List.copyOf(this.entries));
        }
    }
}
