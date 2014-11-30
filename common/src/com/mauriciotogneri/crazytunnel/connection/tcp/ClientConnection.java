package com.mauriciotogneri.crazytunnel.connection.tcp;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class ClientConnection extends Thread
{
	private ClientConnectionEvent clientEvent;
	private InputStream reader;
	private OutputStream writer;
	private Socket socket;
	private final String ip;
	private final int port;
	private boolean isConnected = false;
	
	private static final int BUFFER_SIZE = 1024;
	
	public ClientConnection(String ip, int port, ClientConnectionEvent clientEvent)
	{
		this.ip = ip;
		this.port = port;
		this.clientEvent = clientEvent;
	}
	
	public InetAddress getRemoteAddress()
	{
		return this.socket.getInetAddress();
	}
	
	public void setCallback(ClientConnectionEvent clientEvent)
	{
		this.clientEvent = clientEvent;
	}
	
	public boolean isConnected()
	{
		return ((this.socket != null) && this.isConnected);
	}
	
	public void send(byte[] message)
	{
		try
		{
			if (this.writer != null)
			{
				this.writer.write(message);
				this.writer.flush();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void close()
	{
		this.isConnected = false;
		
		closeSocket(this.socket);
		close(this.reader);
		close(this.writer);
	}
	
	private void closeSocket(Socket socket)
	{
		if (socket != null)
		{
			try
			{
				this.isConnected = false;
				this.socket.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void run()
	{
		try
		{
			InetAddress address = InetAddress.getByName(this.ip);
			this.socket = new Socket(address, this.port);
			
			if (this.socket != null)
			{
				this.reader = this.socket.getInputStream();
				this.writer = this.socket.getOutputStream();
				
				this.isConnected = true;
				
				this.clientEvent.onConnect();
				
				int read = 0;
				byte[] buffer = new byte[ClientConnection.BUFFER_SIZE];
				
				while ((read = this.reader.read(buffer)) != -1)
				{
					if (read > 0)
					{
						try
						{
							this.clientEvent.onReceive(Arrays.copyOfRange(buffer, 0, read));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else
			{
				this.clientEvent.onErrorConnecting();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			disconnected();
			close();
		}
	}
	
	private void disconnected()
	{
		this.isConnected = false;
		this.clientEvent.onDisconnect();
	}
	
	private boolean close(Closeable resource)
	{
		boolean result = false;
		
		if (resource != null)
		{
			try
			{
				resource.close();
				result = true;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public interface ClientConnectionEvent
	{
		void onConnect();
		
		void onErrorConnecting();
		
		void onDisconnect();
		
		void onReceive(byte[] message);
	}
}