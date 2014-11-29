package com.mauriciotogneri.crazytunnel.connection.tcp;

import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread
{
	private final int port;
	private volatile boolean running = true;
	private ServerSocket serverSocket;
	private final ServerEvent serverEvent;
	
	public Server(ServerEvent serverEvent, int port)
	{
		this.serverEvent = serverEvent;
		this.port = port;
	}
	
	@Override
	public void run()
	{
		try
		{
			this.serverSocket = new ServerSocket(this.port);
			
			while (this.running)
			{
				try
				{
					this.serverEvent.onConnected(this.serverSocket.accept());
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			finish();
		}
	}
	
	private void closeServerSocket(ServerSocket socket)
	{
		if ((socket != null) && (!socket.isClosed()))
		{
			try
			{
				socket.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void finish()
	{
		this.running = false;
		closeServerSocket(this.serverSocket);
	}
	
	public interface ServerEvent
	{
		void onConnected(Socket socket);
	}
}