package com.mauriciotogneri.crazytunnel.connection;

import java.net.Socket;
import com.mauriciotogneri.crazytunnel.Player;
import com.mauriciotogneri.crazytunnel.connection.tcp.ServerConnection;
import com.mauriciotogneri.crazytunnel.connection.tcp.ServerConnection.ServerConnectionEvent;
import com.mauriciotogneri.crazytunnel.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.messages.Messages;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerConnect;
import com.mauriciotogneri.crazytunnel.messages.Messages.PlayerInfo;

public class Client implements ServerConnectionEvent
{
	private final Game game;
	private final ServerConnection serverConnection;
	
	public Client(Game game, Socket socket)
	{
		this.game = game;
		this.serverConnection = new ServerConnection(socket, this);
	}
	
	public void start()
	{
		this.serverConnection.start();
	}
	
	public String getRemoteAddress()
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
			send(PlayerInfo.create(player.id, playerConnect.name, player.color));
			this.game.sendPlayerList();
			this.game.checkStartRace();
		}
		else
		{
			// TODO: SEND ERROR CONNECTiNG
		}
	}
}