package com.mauriciotogneri.crazytunnel.common.network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;

public class DatagramCommunication extends Thread
{
	private final DatagramSocket socket;
	private DatagramCommunicationEvent callback;
	private volatile boolean running = true;
	
	private static final int BUFFER_SIZE = 1024;
	
	public DatagramCommunication(int port, DatagramCommunicationEvent callback) throws SocketException
	{
		this.socket = new DatagramSocket(port);
		this.callback = callback;
	}
	
	public DatagramCommunication(DatagramCommunicationEvent callback) throws SocketException
	{
		this.socket = new DatagramSocket();
		this.callback = callback;
	}
	
	public void setCallback(DatagramCommunicationEvent callback)
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
			Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
			
			byte[] buffer = new byte[DatagramCommunication.BUFFER_SIZE];
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
	
	public interface DatagramCommunicationEvent
	{
		void onReceive(InetAddress address, int port, byte[] message);
	}
}