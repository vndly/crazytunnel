package com.mauriciotogneri.crazytunnel.server.core;

import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.mauriciotogneri.crazytunnel.common.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.common.messages.Messages;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerConnect;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerFinished;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication.DatagramCommunicationEvent;
import com.mauriciotogneri.crazytunnel.common.objects.Color;
import com.mauriciotogneri.crazytunnel.common.objects.Player;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;
import com.mauriciotogneri.crazytunnel.server.core.Server.ServerEvent;

public class Game implements ServerEvent, DatagramCommunicationEvent
{
	private final GameEvent gameEvent;
	private final Server server;
	private final DatagramCommunication datagramCommunication;
	
	private int playersReady = 0;
	
	private final Object playerIdLock = new Object();
	private byte nextPlayerId = 1;
	
	private final Object colorLock = new Object();
	private final List<Integer> colorIndex = new ArrayList<Integer>();
	
	private final Object registeredPlayersLock = new Object();
	private final Map<Client, Player> registeredPlayers = new HashMap<Client, Player>();
	
	private final List<RankingRow> ranking = new ArrayList<RankingRow>();
	
	private final int numberOfPlayers;
	private final int numberOfLaps;
	
	public Game(GameEvent gameEvent, int port, int numberOfPlayers, int numberOfLaps) throws SocketException
	{
		this.gameEvent = gameEvent;
		this.server = new Server(this, port);
		this.datagramCommunication = new DatagramCommunication(this);
		
		this.numberOfPlayers = numberOfPlayers;
		this.numberOfLaps = numberOfLaps;
		
		this.colorIndex.add(Color.getColor(60, 170, 230, 255));
		this.colorIndex.add(Color.getColor(255, 60, 60, 255));
		this.colorIndex.add(Color.getColor(100, 200, 100, 255));
		this.colorIndex.add(Color.getColor(255, 200, 40, 255));
		this.colorIndex.add(Color.getColor(230, 110, 240, 255));
		this.colorIndex.add(Color.getColor(130, 230, 230, 255));
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
	public void onClientConnected(Socket socket)
	{
		this.gameEvent.onClientConnected(socket.getInetAddress());
		
		Client client = new Client(this, socket);
		client.start();
	}
	
	@Override
	public void onConnected(InetAddress address, int port)
	{
		this.gameEvent.onConnected(address, port);
	}
	
	@Override
	public void onFinished()
	{
		this.gameEvent.onFinished();
	}
	
	public void clientDisconnect(Client client)
	{
		this.gameEvent.onClientDisconnect(client.getRemoteAddress());
		
		synchronized (this.registeredPlayersLock)
		{
			this.registeredPlayers.remove(client);
		}
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
		
		synchronized (this.registeredPlayersLock)
		{
			if (this.registeredPlayers.size() < this.numberOfPlayers)
			{
				byte id = getPlayerId();
				int color = getPlayerColor();
				
				result = new Player(id, playerConnect.name, color);
				
				this.registeredPlayers.put(client, result);
			}
		}
		
		return result;
	}
	
	public void checkStartRace()
	{
		synchronized (this.registeredPlayersLock)
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
			
			synchronized (this.ranking)
			{
				this.ranking.clear();
			}
			
			this.playersReady = 0;
		}
	}
	
	public void processPlayerFinished(PlayerFinished playerFinished)
	{
		synchronized (this.ranking)
		{
			this.ranking.add(new RankingRow(playerFinished.playerName, playerFinished.playerColor, playerFinished.time));
			
			Collections.sort(this.ranking, new Comparator<RankingRow>()
			{
				@Override
				public int compare(RankingRow o1, RankingRow o2)
				{
					return (o1.time < o2.time) ? -1 : ((o1.time > o2.time) ? 1 : 0);
				}
			});
			
			RankingRow firstPlayer = this.ranking.get(0);
			float lastTime = firstPlayer.time;
			firstPlayer.timeDifference = 0;
			
			for (int i = 1; i < this.ranking.size(); i++)
			{
				RankingRow row = this.ranking.get(i);
				row.timeDifference = row.time - lastTime;
				lastTime = row.time;
			}
			
			sendAllPlayers(Messages.RankingList.create(this.ranking));
		}
	}
	
	public void sendPlayerList()
	{
		synchronized (this.registeredPlayersLock)
		{
			Collection<Player> players = this.registeredPlayers.values();
			
			List<Player> list = new ArrayList<Player>();
			
			for (Player player : players)
			{
				list.add(player);
			}
			
			Collections.sort(list, new Comparator<Player>()
			{
				@Override
				public int compare(Player o1, Player o2)
				{
					return o1.id - o2.id;
				}
			});
			
			sendAllPlayers(Messages.PlayersList.create(this.registeredPlayers.values()));
		}
	}
	
	private void sendStartGame()
	{
		synchronized (this.registeredPlayersLock)
		{
			sendAllPlayers(Messages.StartGame.create(this.numberOfLaps, this.registeredPlayers.values()));
		}
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
		synchronized (this.registeredPlayersLock)
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
		synchronized (this.registeredPlayersLock)
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
	}
	
	public interface GameEvent
	{
		void onConnected(InetAddress address, int port);
		
		void onFinished();
		
		void onClientConnected(InetAddress address);
		
		void onClientDisconnect(InetAddress address);
	}
}