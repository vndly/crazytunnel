package com.mauriciotogneri.crazytunnel.server.core;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerConnection extends Thread
{
	private final ServerConnectionEvent serverEvent;
	private InputStream reader;
	private OutputStream writer;
	private final Socket socket;
	private volatile boolean isConnected = true;
	
	private static final int BUFFER_SIZE = 1024;
	
	public ServerConnection(Socket socket, ServerConnectionEvent serverEvent)
	{
		this.socket = socket;
		this.serverEvent = serverEvent;
	}
	
	public InetAddress getRemoteAddress()
	{
		return this.socket.getInetAddress();
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
		
		close(this.socket);
		close(this.reader);
		close(this.writer);
	}
	
	@Override
	public void run()
	{
		try
		{
			this.reader = this.socket.getInputStream();
			this.writer = this.socket.getOutputStream();
			
			int read = 0;
			byte[] buffer = new byte[ServerConnection.BUFFER_SIZE];
			
			while ((read = this.reader.read(buffer)) != -1)
			{
				if (read > 0)
				{
					try
					{
						this.serverEvent.onReceive(Arrays.copyOfRange(buffer, 0, read));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
			}
			
		}
		catch (SocketException e)
		{
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (this.isConnected)
			{
				this.serverEvent.onDisconnect();
			}
			
			close();
		}
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
	
	public interface ServerConnectionEvent
	{
		void onDisconnect();
		
		void onReceive(byte[] message);
	}
}