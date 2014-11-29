package com.mauriciotogneri.crazytunnel.connection.udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class Connection extends Thread
{
	private final DatagramSocket socket;
	private ConnectionEvent callback;
	private volatile boolean running = true;
	
	private static final int BUFFER_SIZE = 1024;
	
	public Connection(int port, ConnectionEvent callback) throws SocketException
	{
		this.socket = new DatagramSocket(port);
		this.callback = callback;
	}
	
	public Connection(ConnectionEvent callback) throws SocketException
	{
		this.socket = new DatagramSocket();
		this.callback = callback;
	}
	
	public void setCallback(ConnectionEvent callback)
	{
		this.callback = callback;
	}
	
	public int getLocalPort()
	{
		return this.socket.getLocalPort();
	}
	
	@Override
	public void run()
	{
		try
		{
			byte[] buffer = new byte[Connection.BUFFER_SIZE];
			DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
			
			while (this.running)
			{
				this.socket.receive(datagram);
				this.callback.onReceive(datagram.getAddress(), datagram.getPort(), Arrays.copyOfRange(datagram.getData(), 0, datagram.getLength()));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			close();
		}
	}
	
	public void send(InetAddress address, int port, byte[] message)
	{
		DatagramPacket datagram = new DatagramPacket(message, message.length, address, port);
		
		try
		{
			this.socket.send(datagram);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		this.running = false;
		
		if ((this.socket != null) && (!this.socket.isClosed()))
		{
			this.socket.close();
		}
	}
	
	public interface ConnectionEvent
	{
		void onReceive(InetAddress address, int port, byte[] message);
	}
}