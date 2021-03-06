package nova.wrappertests.depmodules;

import nova.core.entity.component.Player;
import nova.core.network.NetworkManager;
import nova.core.network.Packet;
import nova.core.network.PacketHandler;
import se.jbee.inject.bind.BinderModule;

/**
 * @author Calclavia
 */
public class FakeNetworkModule extends BinderModule {
	@Override
	protected void declare() {
		bind(NetworkManager.class).to(FakeNetworkManager.class);
	}

	public static class FakeNetworkManager extends NetworkManager {
		@Override
		public Packet newPacket() {
			return null;
		}

		@Override
		public void sendPacket(PacketHandler sender, Packet packet) {

		}

		@Override
		public void sync(int id, PacketHandler sender) {

		}

		@Override
		public void sendChat(Player player, String message) {

		}

		@Override
		public boolean isServer() {
			return true;
		}
	}
}

