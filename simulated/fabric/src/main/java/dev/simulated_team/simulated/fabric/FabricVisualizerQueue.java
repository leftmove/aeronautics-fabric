package dev.simulated_team.simulated.fabric;

import dev.engine_room.flywheel.lib.visualization.SimpleBlockEntityVisualizer;
import dev.simulated_team.simulated.mixin.accessor.CreateBlockEntityBuilderAccessor;
import com.simibubi.create.foundation.data.CreateBlockEntityBuilder;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class FabricVisualizerQueue {
	private static final List<Runnable> PENDING = new ArrayList<>();

	private FabricVisualizerQueue() {
	}

	public static void queue(final CreateBlockEntityBuilder<?, ?> builder) {
		PENDING.add(() -> apply(builder));
	}

	public static void applyAll() {
		PENDING.forEach(Runnable::run);
		PENDING.clear();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void apply(final CreateBlockEntityBuilder<?, ?> builder) {
		final CreateBlockEntityBuilderAccessor accessor = (CreateBlockEntityBuilderAccessor) builder;
		final NonNullSupplier<SimpleBlockEntityVisualizer.Factory<?>> visualFactory = accessor.getVisualFactory();
		if (visualFactory == null) {
			return;
		}
		final Predicate renderNormally = accessor.getRenderNormally();
		SimpleBlockEntityVisualizer.builder((BlockEntityType) builder.getEntry())
				.factory(visualFactory.get())
				.skipVanillaRender(be -> !renderNormally.test(be))
				.apply();
	}
}
