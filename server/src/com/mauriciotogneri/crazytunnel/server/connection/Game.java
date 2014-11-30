package com.mauriciotogneri.crazytunnel.server.connection;

import java.awt.Color;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mauriciotogneri.crazytunnel.common.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.common.messages.Messages;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerConnect;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication.DatagramCommunicationEvent;
import com.mauriciotogneri.crazytunnel.common.objects.Player;
import com.mauriciotogneri.crazytunnel.server.connection.Server.ServerEvent;

public class Game implements ServerEvent, DatagramCommunicationEvent
{
	private final Server server;
	private final DatagramCommunication datagramCommunication;
	
	private int playersReady = 0;
	
	private final Object playerIdLock = new Object();
	private byte nextPlayerId = 1;
	
	private final Object colorLock = new Object();
	private final List<Integer> colorIndex = new ArrayList<Integer>();
	
	private final Map<Client, Player> registeredPlayers = new HashMap<Client, Player>();
	
	private final int numberOfPlayers;
	private final int numberOfLaps;
	
	public Game(int port, int numberOfPlayers, int numberOfLaps) throws SocketException
	{
		this.server = new Server(this, port);
		this.datagramCommunication = new DatagramCommunication(this);
		
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfLaps = numberOfLaps;
		
		this.colorIndex.add(new Color(60, 170, 230, 255).getRGB());
		this.colorIndex.add(new Color(255, 60, 60, 255).getRGB());
		this.colorIndex.add(new Color(100, 200, 100, 255).getRGB());
		this.colorIndex.add(new Color(255, 200, 40, 255).getRGB());
		this.colorIndex.add(new Color(230, 110, 240, 255).getRGB());
		this.colorIndex.add(new Color(130, 230, 230, 255).getRGB());
		
		try
		{
			log("SERVER STARTED: " + InetAddress.getLocalHost() + ":" + port);
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
		}
	}
	
	public void start()
	{
		this.server.start();
		this.datagramCommunication.start();
	}
	
	public int getUdpPort()
	{
		return this.datagramCommunication.getLocalPort();
	}
	
	@Override
	public void onConnected(Socket socket)
	{
		log("NEW CONNECTION: " + socket.getInetAddress().getHostAddress());
		
		Client client = new Client(this, socket);
		client.start();
	}
	
	@Override
	public void onFinished()
	{
		System.err.println("SERVER FINISHED");
	}
	
	public void clientDisconnect(Client client)
	{
		log("CLIENT DISCONNECTED: " + client.getRemoteAddress().getHostAddress());
		
		this.registeredPlayers.remove(client);
	}
	
	public int getPlayerColor()
	{
		int result = 0;
		
		synchronized (this.colorLock)
		{
			result = this.colorIndex.remove(0);
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
	
	@Override
	public void onReceive(InetAddress address, int port, byte[] message)
	{
		MessageReader reader = new MessageReader(message);
		byte code = reader.getByte();
		
		switch (code)
		{
			case Messages.PlayerBoxPosition.CODE:
				processPlayerBoxPosition(address, message);
				break;
		}
	}
	
	private void processPlayerBoxPosition(InetAddress address, byte[] message)
	{
		Set<Client> clientList = this.registeredPlayers.keySet();
		
		for (Client client : clientList)
		{
			if (!client.getRemoteAddress().equals(address))
			{
				this.datagramCommunication.send(client.getRemoteAddress(), client.getUdpPort(), message);
			}
		}
	}
	
	private void log(String message)
	{
		System.out.println(message);
	}
}