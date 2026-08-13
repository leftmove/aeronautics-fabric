package dev.ryanhcode.offroad.fabric;

import dev.ryanhcode.offroad.Offroad;
import dev.ryanhcode.offroad.events.OffroadCommonEvents;
import dev.ryanhcode.offroad.fabric.service.FabricOffroadConfigService;
import dev.simulated_team.simulated.registrate.RegistrateFlusher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.component.DataComponentMap;

public class OffroadFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		Offroad.init();
		RegistrateFlusher.flush(Offroad.getRegistrate());
		FabricOffroadConfigService.register();

		DefaultItemComponentEvents.MODIFY.register(context -> OffroadCommonEvents.modifyDefaultComponents((itemLike, patchConsumer) ->
				context.modify(itemLike.asItem(), builder -> applyPatch(builder, patchConsumer))));

		ServerTickEvents.END_WORLD_TICK.register(OffroadCommonEvents::tickLevelEvent);
	}

	private static DataComponentMap.Builder applyPatch(final DataComponentMap.Builder builder, final java.util.function.Consumer<DataComponentPatch.Builder> patchConsumer) {
		final DataComponentPatch.Builder patchBuilder = DataComponentPatch.builder();
		patchConsumer.accept(patchBuilder);
		final PatchedDataComponentMap map = new PatchedDataComponentMap(DataComponentMap.EMPTY);
		map.applyPatch(patchBuilder.build());
		for (final TypedDataComponent<?> component : map) {
			set(builder, component);
		}
		return builder;
	}

	private static <T> void set(final DataComponentMap.Builder builder, final TypedDataComponent<T> component) {
		builder.set(component.type(), component.value());
	}
}
