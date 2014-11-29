package com.mauriciotogneri.crazytunnel.connection;

import java.awt.Color;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mauriciotogneri.crazytunnel.Player;
import com.mauriciotogneri.crazytunnel.connection.tcp.Server;
import com.mauriciotogneri.crazytunnel.connection.tcp.Server.ServerEvent;
import com.mauriciotogneri.crazytunnel.messages.Messages;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerConnect;

public class Game implements ServerEvent
{
	private final Server server;
	
	private int playersReady = 0;
	
	private final Object playerIdLock = new Object();
	private byte nextPlayerId = 1;
	
	private final Object colorLock = new Object();
	private final List<Integer> colorIndex = new ArrayList<Integer>();
	
	private final Map<Client, Player> registeredPlayers = new HashMap<Client, Player>();
	
	private final int numberOfPlayers;
	private final int numberOfLaps;
	
	public Game(int port, int numberOfPlayers, int numberOfLaps)
	{
		this.server = new Server(this, port);
		
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfLaps = numberOfLaps;
		
		this.colorIndex.add(new Color(255, 60, 170, 230).getRGB());
		this.colorIndex.add(new Color(255, 255, 60, 60).getRGB());
		this.colorIndex.add(new Color(255, 100, 200, 100).getRGB());
		this.colorIndex.add(new Color(255, 255, 200, 40).getRGB());
		this.colorIndex.add(new Color(255, 230, 110, 240).getRGB());
		this.colorIndex.add(new Color(255, 130, 230, 230).getRGB());
		
		try
		{
			System.out.println(InetAddress.getLocalHost() + ":" + port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		this.server.start();
	}
	
	@Override
	public void onConnected(Socket socket)
	{
		System.out.println("NEW CONNECTION: " + socket.getRemoteSocketAddress());
		
		Client client = new Client(this, socket);
		client.start();
	}
	
	public void clientDisconnect(Client client)
	{
		System.out.println("CLIENT DISCONNECTED: " + client.getRemoteAddress());
		
		this.registeredPlayers.remove(client);
	}
	
	public int getPlayerColor()
	{
		int result = 0;
		
		synchronized (this.colorLock)
		{
			result = this.colorIndex.get(0);
			this.colorIndex.remove(0);
		}
		
		return result;
	}
	
	public byte getPlayerId()
	{
		byte result = 0;
		
		synchronized (this.playerIdLock)
		{
			result = this.nextPlayerId++;
		}
		
		return result;
	}
	
	public Player processPlayerConnect(Client client, PlayerConnect playerConnect)
	{
		Player result = null;
		
		if (this.registeredPlayers.size() < this.numberOfPlayers)
		{
			byte id = getPlayerId();
			int color = getPlayerColor();
			
			result = new Player(id, playerConnect.name, color);
			
			this.registeredPlayers.put(client, result);
		}
		
		return result;
	}
	
	public void checkStartRace()
	{
		if (this.registeredPlayers.size() == this.numberOfPlayers)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			sendStartGame();
		}
	}
	
	public void processReady()
	{
		this.playersReady++;
		
		if (this.playersReady == this.numberOfPlayers)
		{
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			
			sendStartRace();
		}
	}
	
	public void processPlayerBoxPosition(Client client, PlayerBoxPosition playerBoxPosition)
	{
		sendAllPlayers(client, playerBoxPosition.create());
	}
	
	public void sendPlayerList()
	{
		sendAllPlayers(Messages.PlayersList.create(this.registeredPlayers.values()));
	}
	
	private void sendStartGame()
	{
		sendAllPlayers(Messages.StartGame.create(this.numberOfLaps, this.registeredPlayers.values()));
	}
	
	private void sendStartRace()
	{
		sendAllPlayers(Messages.StartRace.create());
	}
	
	private void sendAllPlayers(byte[] message)
	{
		sendAllPlayers(null, message);
	}
	
	private void sendAllPlayers(Client excludeClient, byte[] message)
	{
		Set<Client> clientList = this.registeredPlayers.keySet();
		
		for (Client client : clientList)
		{
			if (client != excludeClient)
			{
				client.send(message);
			}
		}
	}
}