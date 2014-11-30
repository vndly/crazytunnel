package com.mauriciotogneri.crazytunnel.connection;

import java.net.InetAddress;
import java.net.Socket;
import com.mauriciotogneri.crazytunnel.connection.ServerConnection.ServerConnectionEvent;
import com.mauriciotogneri.crazytunnel.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.messages.Messages;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerConnect;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerInfo;
import com.mauriciotogneri.crazytunnel.objects.Player;

public class Client implements ServerConnectionEvent
{
	private final Game game;
	private final ServerConnection serverConnection;
	private int udpPort = 0;
	
	public Client(Game game, Socket socket)
	{
		this.game = game;
		this.serverConnection = new ServerConnection(socket, this);
	}
	
	public void start()
	{
		this.serverConnection.start();
	}
	
	public int getUdpPort()
	{
		return this.udpPort;
	}
	
	public InetAddress getRemoteAddress()
	{
		return this.serverConnection.getRemoteAddress();
	}
	
	@Override
	public void onDisconnect()
	{
		this.game.clientDisconnect(this);
	}
	
	@Override
	public void onReceive(byte[] message)
	{
		MessageReader reader = new MessageReader(message);
		byte code = reader.getByte();
		
		switch (code)
		{
			case Messages.PlayerConnect.CODE:
				processPlayerConnect(new PlayerConnect(reader));
				break;
			
			case Messages.Ready.CODE:
				this.game.processReady();
				break;
			
			case Messages.PlayerBoxPosition.CODE:
				this.game.processPlayerBoxPosition(this, new PlayerBoxPosition(reader));
				break;
		}
	}
	
	public void send(byte[] message)
	{
		this.serverConnection.send(message);
	}
	
	private void processPlayerConnect(PlayerConnect playerConnect)
	{
		Player player = this.game.processPlayerConnect(this, playerConnect);
		
		if (player != null)
		{
			this.udpPort = playerConnect.udpPort;
			
			send(PlayerInfo.create(player.id, playerConnect.name, player.color, this.game.getUdpPort()));
			this.game.sendPlayerList();
			this.game.checkStartRace();
		}
		else
		{
			// TODO: SEND ERROR CONNECTiNG
		}
	}
}