package de.flaflo.game.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.flaflo.game.server.Server;
import de.flaflo.game.server.entity.Player;

public class S05PacketLeave extends Packet {

	private Player player;
	
	public S05PacketLeave(Player player) {
		super((byte) 5);
		this.player = player;
	}

	@Override
	public void send(DataOutputStream out) throws IOException {
		out.writeByte(id);
		out.writeInt(player.getId());
	}

	@Override
	public void receive(DataInputStream in) throws IOException {
		int id = in.readInt();
		
		this.setPlayer(Server.getServer().getPlayerByID(id));
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

}
