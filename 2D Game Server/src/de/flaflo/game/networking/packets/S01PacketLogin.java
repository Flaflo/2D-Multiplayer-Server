package de.flaflo.game.networking.packets;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class S01PacketLogin extends Packet {
	
	private String name;
	private Color color;
	
	private int x, y;
	
	public S01PacketLogin() {
		super((byte) 1);
	}

	@Override
	public void send(DataOutputStream out) throws IOException { }

	@Override
	public void receive(DataInputStream in) throws IOException { 
		String name = in.readUTF();
		this.setName(name);
		
		int x = in.readInt();
		int y = in.readInt();
		
		this.setX(x);
		this.setY(y);
		
		int cRed = in.readInt();
		int cGreen = in.readInt();
		int cBlue = in.readInt();
		
		this.setColor(new Color(cRed, cGreen, cBlue));
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return x;
	}

	/**
	 * @param x the x to set
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
	 * @param y the y to set
	 */
	public void setY(int y) {
		this.y = y;
	}
}
