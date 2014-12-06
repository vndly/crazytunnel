package com.mauriciotogneri.crazytunnel.server.desktop;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import com.mauriciotogneri.crazytunnel.server.core.Game;
import com.mauriciotogneri.crazytunnel.server.core.Game.GameEvent;

public class CrazyTunnelServerDesktop implements GameEvent
{
	public static void main(String[] args)
	{
		CrazyTunnelServerDesktop crazyTunnelServer = new CrazyTunnelServerDesktop();
		crazyTunnelServer.start(args);
	}
	
	public void start(String[] args)
	{
		try
		{
			if (args.length > 0)
			{
				Properties properties = CrazyTunnelServerDesktop.getProperties(args[0]);
				
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
	public void onPlayerConnected(InetAddress address, String name)
	{
		System.out.println("PLAYER CONNECTED: " + name + " (" + address.getHostAddress() + ")");
	}
	
	@Override
	public void onPlayerDisconnect(String name)
	{
		System.out.println("PLAYER DISCONNECTED: " + name);
	}
	
	@Override
	public void onConnected(InetAddress address, int port)
	{
		System.out.println("SERVER STARTED: " + address + ":" + port);
	}
	
	@Override
	public void onFinished()
	{
		System.err.println("SERVER CLOSED");
	}
	
	@Override
	public void onStartGame()
	{
		System.err.println("GAME STARTED");
	}
	
	@Override
	public void onStartRace()
	{
		System.err.println("RACE STARTED");
	}
	
	@Override
	public void onPlayerFinished(String name)
	{
		System.err.println("PLAYER FINISHED: " + name);
	}
	
	@Override
	public void onPlayerReady(String name)
	{
		System.err.println("PLAYER READY: " + name);
	}
}