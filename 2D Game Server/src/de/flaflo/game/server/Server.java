package de.flaflo.game.server;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 			  TODO
 * 		</br>
 * 		 • Packet System</br>
 *  		- Packet IDs</br>
 *  		- Sending Packet Objects</br>
 *  		- Packet Receive Events</br>
 * 		</br>
 * 		 • Player IDs
 * 		</br>
 * @author Flaflo
 *
 */
public class Server implements Runnable {

	public static void main(String[] args) throws IOException {
		new Server();
	}

	private CopyOnWriteArrayList<Player> players;
	
	private static Server instance;
	
	public static Server getServer() {
		return instance;
	}
	
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

	public static final int PORT = 1338;

	private ServerSocket socket;

	public Server() throws IOException {
		instance = this;
		
		players = new CopyOnWriteArrayList<Player>();
		this.start();
	}

	public synchronized void start() throws IOException {
		if (socket == null)
			socket = new ServerSocket(PORT);
			
		this.run();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket soc = socket.accept();
				
				DataOutputStream out = new DataOutputStream(soc.getOutputStream());
				DataInputStream in = new DataInputStream(soc.getInputStream());

				String name = in.readUTF();
				int x = in.readInt();
				int y = in.readInt();
				
				int cRed = in.readInt();
				int cGreen = in.readInt();
				int cBlue = in.readInt();

				out.writeInt(this.players.size());

				for (Player p : this.players) {
					out.writeUTF(p.getName());
					out.writeInt(p.getX());
					out.writeInt(p.getY());
					
					out.writeInt(p.getColor().getRed());
					out.writeInt(p.getColor().getGreen());
					out.writeInt(p.getColor().getBlue());
				}
				
				for (Player p : this.players) {
					DataOutputStream pOut = new DataOutputStream(p.getSocket().getOutputStream());
					
					pOut.writeUTF("addPlayer");
					pOut.writeUTF(name);
					pOut.writeInt(x);
					pOut.writeInt(y);
					
					pOut.writeInt(cRed);
					pOut.writeInt(cGreen);
					pOut.writeInt(cBlue);
				}
				
				this.players.add(new Player(soc, name, new Color(cRed, cGreen, cBlue), x, y));
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
}
