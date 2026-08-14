package foundry.veil.api.network;

import dev.simulated_team.simulated.service.ServiceUtil;

public interface VeilPacketBackend {
	VeilPacketBackend INSTANCE = ServiceUtil.load(VeilPacketBackend.class);

	VeilPacketChannel createChannel(String modId, String version);
}
