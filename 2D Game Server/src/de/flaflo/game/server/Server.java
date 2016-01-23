package de.flaflo.game.server;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

import de.flaflo.game.server.entity.Player;
import sun.util.calendar.CalendarUtils;

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
@SuppressWarnings("restriction")
public class Server implements Runnable {

	public static void main(String[] args) throws IOException {
		new Server();
	}

	private CopyOnWriteArrayList<Player> players;
	
	private static Server instance;
	
	public static Server getServer() {
		return instance;
	}
	
	public static final int PORT = 1338;

	private ServerSocket socket;

	public Server() throws IOException {
		instance = this;
		
		players = new CopyOnWriteArrayList<Player>();
		
		new Thread() {
			@SuppressWarnings("resource")
			@Override
			public void run() {
				while (true) {
					Scanner scanner = new Scanner(System.in);
					String line = scanner.nextLine();
					String[] args = line.split(" ");

					if (line.startsWith("teleport")) {
						if (args.length != 4) {
							log("Richtige Syntax: teleport <Spielername> <X> <Y>");
							return;
						}
						try {
							Player targetPlayer = getPlayerByName(args[1]);
							
							if (targetPlayer == null) {
								log("Spieler konnte nicht gefunden werden.");
								return;
							}
							
							targetPlayer.teleport(Integer.parseInt(args[2]), Integer.parseInt(args[3]));
						} catch (Exception ex) {
							log("Ungültige Zeichen erkannt.");
						}
					} else
						log("Diesen Command kenne ich nicht.");
				}
			}
		}.start();
		
		this.start();
	}

	public Player getPlayerByName(String name) {
		for (Player p : this.players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		
		return null;
	}
	
	public synchronized void start() throws IOException {
		if (socket == null)
			socket = new ServerSocket(PORT);
			
		log("Server gestartet (Port: " + PORT + ")");

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
				
				log(name + " betritt das Spiel");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void log(String text) {
	    Date date = Date.from(Instant.now());
		
		StringBuilder sb = new StringBuilder();
		
		CalendarUtils.sprintf0d(sb, date.getHours(), 2).append(':');
	    CalendarUtils.sprintf0d(sb, date.getMinutes(), 2).append(':');
	    CalendarUtils.sprintf0d(sb, date.getSeconds(), 2);
	    
		System.out.println("{[" + sb.toString() + "] Server}: " + text);
	}
	
	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
}
