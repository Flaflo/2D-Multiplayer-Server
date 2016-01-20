package de.flaflo.game.server.entity;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import de.flaflo.game.server.Server;

public class Player implements Runnable {

	private Socket socket;

	private Thread innerThread;

	private boolean isRunning;
	
	private String name;
	private int x, y;
	private Color color;

	public Player(Socket socket, String name, Color color, int x, int y) {
		this.socket = socket;
		
		this.name = name;
		
		this.color = color;
		
		this.x = x;
		this.y = y;
		
		innerThread = new Thread(this);
		innerThread.start();
	}

	@SuppressWarnings("unused")
	@Override
	public void run() {
		isRunning = true;

		while (isRunning) {
			try {
				DataInputStream in = new DataInputStream(socket.getInputStream());
				DataOutputStream out = new DataOutputStream(socket.getOutputStream());

				String desire = in.readUTF();

				switch (desire) {
					case "posUpdate":
					int x = in.readInt();
					int y = in.readInt();
					
					this.setX(x);
					this.setY(y);

					for (Player p : Server.getServer().getPlayers()) {
						DataOutputStream pOut = new DataOutputStream(p.getSocket().getOutputStream());
						
						pOut.writeUTF("posUpdate");
						pOut.writeUTF(this.getName());
						pOut.writeInt(x);							
						pOut.writeInt(y);
					}

					break;
				}
			} catch (IOException e) {
				this.isRunning = false;
				Server.getServer().getPlayers().remove(this);

				for (Player p : Server.getServer().getPlayers()) {
					try {
						DataOutputStream out = new DataOutputStream(p.getSocket().getOutputStream());
						out.writeUTF("removePlayer");
						out.writeUTF(this.getName());
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * @return the socket
	 */
	public Socket getSocket() {
		return socket;
	}

	/**
	 * @param socket the socket to set
	 */
	public void setSocket(Socket socket) {
		this.socket = socket;
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

	public Color getColor() {
		return color;
	}
	
	/**
	 * @return the isRunning
	 */
	public boolean isRunning() {
		return isRunning;
	}
}
