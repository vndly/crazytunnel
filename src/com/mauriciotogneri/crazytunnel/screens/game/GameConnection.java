package com.mauriciotogneri.crazytunnel.screens.game;

import android.bluetooth.BluetoothDevice;
import com.mauriciotogneri.bluetooth.connection.client.ClientConnection;
import com.mauriciotogneri.bluetooth.connection.client.ClientEvent;
import com.mauriciotogneri.bluetooth.connection.server.ServerConnection;
import com.mauriciotogneri.bluetooth.connection.server.ServerEvent;

public class GameConnection implements ClientEvent, ServerEvent
{
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
		// TODO
	}
	
	public interface GameEvent
	{
		
	}
	
	// ===================================================================
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
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
	}
	
	// ===================================================================
	
	@Override
	public void onReceive(byte[] message)
	{
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
	}
}