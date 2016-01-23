package de.flaflo.game.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.flaflo.game.server.entity.Player;

public class S03PacketAddPlayer extends Packet {

	private Player player;

	public S03PacketAddPlayer(Player player) {
		super((byte) 3);

		this.player = player;
	}

	@Override
	public void send(DataOutputStream out) throws IOException {
		out.writeByte(id);
		out.writeUTF(player.getName());
		out.writeInt(player.getId());
		out.writeInt(player.getX());
		out.writeInt(player.getY());

		out.writeInt(player.getColor().getRed());
		out.writeInt(player.getColor().getGreen());
		out.writeInt(player.getColor().getBlue());
	}

	@Override
	public void receive(DataInputStream in) throws IOException {

	}

}
