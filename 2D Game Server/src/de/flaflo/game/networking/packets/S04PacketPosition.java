package de.flaflo.game.networking.packets;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import de.flaflo.game.networking.entity.Player;

public class S04PacketPosition extends Packet {

	private Player player;
	private int x, y;
	
	public S04PacketPosition(Player player, int x, int y) {
		super((byte) 4);
		this.player = player;
		this.x = x;
		this.y = y;
	}
	
	public S04PacketPosition() {
		super((byte) 4);
	}

	@Override
	public void send(DataOutputStream out) throws IOException {
		out.writeByte(id);
		out.writeInt(player.getId());
		out.writeInt(x);
		out.writeInt(y);
	}

	@Override
	public void receive(DataInputStream in) throws IOException {
		int x = in.readInt();
		int y = in.readInt();

		this.setX(x);
		this.setY(y);
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player
	 *            the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		this.x = x;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return y;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}
