package com.mauriciotogneri.crazytunnel.connection.tcp;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Arrays;

public class ServerConnection extends Thread
{
	private final ServerConnectionEvent serverEvent;
	private InputStream reader;
	private OutputStream writer;
	private final Socket socket;
	private boolean isConnected = true;
	
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
				
				// System.out.println(">>> " + getRemoteAddress() + " = " + Arrays.toString(message));
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
			byte[] buffer = new byte[1024];
			
			while ((read = this.reader.read(buffer)) != -1)
			{
				if (read > 0)
				{
					try
					{
						this.serverEvent.onReceive(Arrays.copyOfRange(buffer, 0, read));
						
						// System.out.println("<<< " + getRemoteAddress() + " = " +
						// Arrays.toString(Arrays.copyOfRange(buffer, 0, read)));
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
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
		this.serverEvent.onDisconnect();
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