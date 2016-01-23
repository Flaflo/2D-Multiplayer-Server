package de.flaflo.game.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.flaflo.game.server.entity.Player;

public class S02PacketPlayerList extends Packet {

	private Player[] players;
	
	public S02PacketPlayerList(Player[] players) {
		super((byte) 2);
		this.players = players;
	}

	@Override
	public void send(DataOutputStream out) throws IOException {
		out.writeInt(players.length);

		for (Player p : players) {
			out.writeUTF(p.getName());
			out.writeInt(p.getX());
			out.writeInt(p.getY());
			
			out.writeInt(p.getColor().getRed());
			out.writeInt(p.getColor().getGreen());
			out.writeInt(p.getColor().getBlue());
		}
	}

	@Override
	public void receive(DataInputStream in) throws IOException { }

	/**
	 * @return the players
	 */
	public Player[] getPlayers() {
		return players;
	}

	/**
	 * @param players the players to set
	 */
	public void setPlayers(Player[] players) {
		this.players = players;
	}
}
