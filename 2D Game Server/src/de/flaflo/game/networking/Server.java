package de.flaflo.game.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import de.flaflo.game.networking.entity.Player;
import de.flaflo.game.networking.packets.Packet;
import de.flaflo.game.networking.packets.S01PacketLogin;
import de.flaflo.game.networking.packets.S02PacketPlayerList;
import de.flaflo.game.networking.packets.S03PacketAddPlayer;
import sun.util.calendar.CalendarUtils;

/**
 * 
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
			
		log("Server started (Port: " + PORT + ")");

		this.run();
	}

	@Override
	public void run() {
		while (true) {
			try {
				Socket soc = socket.accept();
				
				S01PacketLogin loginPacket = new S01PacketLogin();
				loginPacket.receive(new DataInputStream(soc.getInputStream()));
				Player player = new Player((players.isEmpty() ? 0 : players.size() + 1), soc, loginPacket.getName(), loginPacket.getColor(), loginPacket.getMass(), loginPacket.getX(), loginPacket.getY());
				
				this.sendPacket(player, new S02PacketPlayerList(this.players.toArray(new Player[this.players.size()])));
				this.sendPacketToAll(new S03PacketAddPlayer(player));
				
				players.add(player);
				
				log(player.getName() + " joined the Game.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void sendPacket(Player player, Packet packet) {
		try {
			DataOutputStream out = new DataOutputStream(player.getSocket().getOutputStream());
			packet.send(out);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void sendPacketToAll(Packet packet) {
		for (Player player : this.players)
			sendPacket(player, packet);
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
	
	public Player getPlayerByName(String name) {
		for (Player p : this.players) {
			if (p.getName().equals(name)) {
				return p;
			}
		}
		
		return null;
	}
	
	public Player getPlayerByID(int id) {
		for (Player p : this.players) {
			if (p.getId() == id) {
				return p;
			}
		}
		
		return null;
	}
	
	public static Server getServer() {
		return instance;
	}
	
	public CopyOnWriteArrayList<Player> getPlayers() {
		return players;
	}
}
