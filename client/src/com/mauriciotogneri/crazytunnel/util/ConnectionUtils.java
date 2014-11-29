package com.mauriciotogneri.crazytunnel.util;

import java.net.InetAddress;
import com.mauriciotogneri.crazytunnel.connection.tcp.ClientConnection;
import com.mauriciotogneri.crazytunnel.connection.udp.Connection;

public class ConnectionUtils
{
	public static void send(final ClientConnection connection, final byte[] message)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				connection.send(message);
			}
		});
		thread.start();
	}
	
	public static void send(final Connection connection, final InetAddress address, final int port, final byte[] message)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				connection.send(address, port, message);
			}
		});
		thread.start();
	}
}