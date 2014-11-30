package com.mauriciotogneri.crazytunnel.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.mauriciotogneri.crazytunnel.connection.Game;

public class CrazyTunnelServer
{
	public static void main(String[] args)
	{
		try
		{
			if (args.length > 0)
			{
				Properties properties = CrazyTunnelServer.getProperties(args[0]);
				
				int port = Integer.parseInt(properties.getProperty("port"));
				int players = Integer.parseInt(properties.getProperty("players"));
				int laps = Integer.parseInt(properties.getProperty("laps"));
				
				Game game = new Game(port, players, laps);
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
	
}