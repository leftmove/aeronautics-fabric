package dev.simulated_team.simulated.data.advancements;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SimpleSimulatedTrigger extends SimulatedCriterionTriggerBase<SimulatedCriterionTriggerBase.Instance> {
    public SimpleSimulatedTrigger(final ResourceLocation id) {
        super(id);
    }

    public void trigger(final ServerPlayer player) {
        super.trigger(player, null);
    }

    public Instance instance() {
        return new Instance(this.getId());
    }

    @Override
    protected Instance createDefaultInstance() {
        return this.instance();
    }

    public static class Instance extends SimulatedCriterionTriggerBase.Instance {

        public Instance(final ResourceLocation id) {
            super(id);
        }

        @Override
        public boolean test(@Nullable final List<Supplier<Object>> suppliers) {
            return true;
        }
    }
}