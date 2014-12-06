package com.mauriciotogneri.crazytunnel.server.desktop;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.mauriciotogneri.crazytunnel.server.core.Game;
import com.mauriciotogneri.crazytunnel.server.core.Game.GameEvent;

public class CrazyTunnelServerWeb implements GameEvent
{
	private int players = 0;
	private int laps = 0;
	private BufferedWriter eventFile = null;
	
	public static void main(String[] args)
	{
		CrazyTunnelServerWeb crazyTunnelServerWeb = new CrazyTunnelServerWeb();
		crazyTunnelServerWeb.start(args);
	}
	
	public void start(String[] args)
	{
		try
		{
			this.players = Integer.parseInt(args[0]);
			this.laps = Integer.parseInt(args[1]);
			this.eventFile = getFile(args[2]);
			
			Game game = new Game(this, 0, this.players, this.laps);
			game.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Override
	public void onConnected(InetAddress address, int port)
	{
		writeFile("SERVER STARTED: " + address + ":" + port);
		writeFile("PLAYERS: " + this.players + " - LAPS: " + this.laps);
	}
	
	@Override
	public void onPlayerConnected(InetAddress address, String name)
	{
		writeFile("PLAYER CONNECTED: " + name + " (" + address.getHostAddress() + ")");
	}
	
	@Override
	public void onPlayerDisconnect(String name)
	{
		writeFile("PLAYER DISCONNECTED: " + name);
	}
	
	@Override
	public void onStartGame()
	{
		writeFile("GAME STARTED");
	}
	
	@Override
	public void onStartRace()
	{
		writeFile("RACE STARTED");
	}
	
	@Override
	public void onPlayerFinished(String name)
	{
		writeFile("PLAYER FINISHED: " + name);
	}
	
	@Override
	public void onPlayerReady(String name)
	{
		writeFile("PLAYER READY: " + name);
	}
	
	@Override
	public void onFinished()
	{
		writeFile("SERVER CLOSED");
		
		if (this.eventFile != null)
		{
			close(this.eventFile);
		}
		
		System.exit(0);
	}
	
	@SuppressWarnings("resource")
	private BufferedWriter getFile(String filePath)
	{
		BufferedWriter result = null;
		
		FileOutputStream stream = null;
		FileChannel channel = null;
		
		try
		{
			File file = new File(filePath);
			
			if (!file.exists())
			{
				file.createNewFile();
			}
			else
			{
				stream = new FileOutputStream(file);
				channel = stream.getChannel();
				channel.truncate(0);
				result = new BufferedWriter(new FileWriter(file));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close(stream);
			close(channel);
		}
		
		return result;
	}
	
	private void writeFile(String content)
	{
		try
		{
			System.out.println(content);
			
			this.eventFile.append(getDate() + "   " + content + "\r\n");
			this.eventFile.flush();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String getDate()
	{
		String result = "";
		
		try
		{
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault());
			result = formatter.format(new Date());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	private void close(Closeable resource)
	{
		if (resource != null)
		{
			try
			{
				resource.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}