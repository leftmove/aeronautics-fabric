package dev.ryanhcode.offroad.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.ryanhcode.offroad.Offroad;
import net.createmod.catnip.config.ui.BaseConfigScreen;

public class OffroadModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BaseConfigScreen(parent, Offroad.MOD_ID);
	}
}
