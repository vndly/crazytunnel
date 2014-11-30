package com.mauriciotogneri.crazytunnel.server.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import com.mauriciotogneri.crazytunnel.server.connection.Game;
import com.mauriciotogneri.crazytunnel.server.connection.Game.GameEvent;

public class CrazyTunnelServer implements GameEvent
{
	public static void main(String[] args)
	{
		CrazyTunnelServer crazyTunnelServer = new CrazyTunnelServer();
		crazyTunnelServer.start(args);
	}
	
	public void start(String[] args)
	{
		try
		{
			if (args.length > 0)
			{
				Properties properties = CrazyTunnelServer.getProperties(args[0]);
				
				int port = Integer.parseInt(properties.getProperty("port"));
				int players = Integer.parseInt(properties.getProperty("players"));
				int laps = Integer.parseInt(properties.getProperty("laps"));
				
				Game game = new Game(this, port, players, laps);
				game.start();
			}
			else
			{
				System.err.println("Configuration file missing");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static Properties getProperties(String path) throws IOException
	{
		Properties result = new Properties();
		
		InputStream inputStream = null;
		
		try
		{
			inputStream = new FileInputStream(path);
			result.load(inputStream);
		}
		finally
		{
			if (inputStream != null)
			{
				inputStream.close();
			}
		}
		
		return result;
	}
	
	@Override
	public void onClientConnected(InetAddress address)
	{
		System.out.println("NEW CONNECTION: " + address.getHostAddress());
	}
	
	@Override
	public void onClientDisconnect(InetAddress address)
	{
		System.out.println("CLIENT DISCONNECTED: " + address.getHostAddress());
	}
	
	@Override
	public void onConnected(InetAddress address, int port)
	{
		System.out.println("SERVER STARTED: " + address + ":" + port);
	}
}