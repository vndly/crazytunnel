package com.mauriciotogneri.crazytunnel.server.core;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

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
			
			this.serverEvent.onConnected(getAddress(), this.serverSocket.getLocalPort());
			
			while (this.running)
			{
				try
				{
					this.serverEvent.onClientConnected(this.serverSocket.accept());
				}
				catch (SocketException e)
				{
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
			if (this.running)
			{
				this.serverEvent.onFinished();
			}
		}
	}
	
	private InetAddress getAddress()
	{
		InetAddress result = null;
		
		try
		{
			for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements();)
			{
				NetworkInterface networkInterface = interfaces.nextElement();
				
				for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements();)
				{
					InetAddress address = addresses.nextElement();
					
					if ((!address.isLoopbackAddress()) && (address.isSiteLocalAddress()))
					{
						return address;
					}
				}
			}
			
			result = InetAddress.getLocalHost();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
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
		if (this.running)
		{
			this.running = false;
			closeServerSocket(this.serverSocket);
		}
	}
	
	public interface ServerEvent
	{
		void onConnected(InetAddress address, int port);
		
		void onClientConnected(Socket socket);
		
		void onFinished();
	}
}