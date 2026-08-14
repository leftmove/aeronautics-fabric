package dev.eriksonn.aeronautics.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import dev.eriksonn.aeronautics.Aeronautics;
import net.createmod.catnip.config.ui.BaseConfigScreen;

public class AeronauticsModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BaseConfigScreen(parent, Aeronautics.MOD_ID);
	}
}
