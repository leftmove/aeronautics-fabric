package dev.simulated_team.simulated.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.createmod.catnip.config.ui.BaseConfigScreen;
import dev.simulated_team.simulated.Simulated;

public class SimulatedModMenu implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new BaseConfigScreen(parent, Simulated.MOD_ID);
	}
}
