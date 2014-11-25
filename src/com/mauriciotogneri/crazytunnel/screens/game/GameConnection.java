package com.mauriciotogneri.crazytunnel.screens.game;

import android.bluetooth.BluetoothDevice;
import com.mauriciotogneri.bluetooth.connection.client.ClientConnection;
import com.mauriciotogneri.bluetooth.connection.client.ClientEvent;
import com.mauriciotogneri.bluetooth.connection.server.ServerConnection;
import com.mauriciotogneri.bluetooth.connection.server.ServerEvent;

public class GameConnection implements ClientEvent, ServerEvent
{
	private GameEvent gameEvent;
	private final ServerConnection serverConnection;
	private final ClientConnection clientConnection;
	
	public GameConnection(ServerConnection serverConnection)
	{
		this.serverConnection = serverConnection;
		this.clientConnection = null;
	}
	
	public GameConnection(ClientConnection clientConnection)
	{
		this.clientConnection = clientConnection;
		this.serverConnection = null;
	}
	
	public void setListener(GameEvent gameEvent)
	{
		this.gameEvent = gameEvent;
		
		if (isClient())
		{
			this.clientConnection.setListener(this);
		}
		else
		{
			this.serverConnection.setListener(this);
		}
	}
	
	public String getMacAddress()
	{
		if (isClient())
		{
			return this.clientConnection.getDeviceAddress();
		}
		else
		{
			return this.serverConnection.getDeviceAddress();
		}
	}
	
	public boolean isClient()
	{
		return this.clientConnection != null;
	}
	
	public boolean isServer()
	{
		return this.serverConnection != null;
	}
	
	public void send(byte[] message, boolean force)
	{
		if (isClient())
		{
			this.clientConnection.send(message, force);
		}
		else
		{
			this.serverConnection.sendAll(message, force);
		}
	}
	
	public void send(String macAddress, byte[] message, boolean force)
	{
		if (isServer())
		{
			this.serverConnection.sendAll(macAddress, message, force);
		}
	}
	
	// ========================= SERVER ============================
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		this.gameEvent.onReceive(message);
	}
	
	@Override
	public void onConnect(BluetoothDevice device)
	{
	}
	
	@Override
	public void onErrorOpeningConnection()
	{
	}
	
	@Override
	public void onDisconnect(BluetoothDevice device)
	{
		this.gameEvent.playerDisconnect(device.getAddress());
	}
	
	// =========================== CLIENT ==========================
	
	@Override
	public void onReceive(byte[] message)
	{
		this.gameEvent.onReceive(message);
	}
	
	@Override
	public void onConnect()
	{
	}
	
	@Override
	public void onErrorConnecting()
	{
	}
	
	@Override
	public void onDisconnect()
	{
		this.gameEvent.onDisconnect();
	}
}