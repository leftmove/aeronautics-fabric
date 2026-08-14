package dev.simulated_team.simulated.fabric.service;

import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.LinkedTypewriterBlockEntity;
import dev.simulated_team.simulated.content.blocks.redstone.linked_typewriter.screen.LinkedTypewriterMenuCommon;
import dev.simulated_team.simulated.fabric.content.linked_typewriter.LinkedTypewriterMenuImpl;
import dev.simulated_team.simulated.service.SimMenuService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Consumer;

public class FabricSimMenuService implements SimMenuService {

	@Override
	@SuppressWarnings("unchecked")
	public <T extends LinkedTypewriterMenuCommon> T getLoaderLinkedTypewriter(final MenuType<?> type, final int id, final Inventory inv, final FriendlyByteBuf extraData) {
		return (T) new LinkedTypewriterMenuImpl(type, id, inv, extraData);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T extends LinkedTypewriterMenuCommon> T getLoaderLinkedTypewriter(final MenuType<?> type, final int id, final Inventory inv, final LinkedTypewriterBlockEntity be) {
		return (T) new LinkedTypewriterMenuImpl(type, id, inv, be);
	}

	@Override
	public void openScreen(final ServerPlayer player, final MenuProvider factory, final Consumer<FriendlyByteBuf> extraDataWriter) {
		try {
			player.getClass().getMethod("openMenu", MenuProvider.class, Consumer.class).invoke(player, factory, extraDataWriter);
		} catch (final ReflectiveOperationException ignored) {
			player.openMenu(factory);
		}
	}
}
