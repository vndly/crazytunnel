package com.mauriciotogneri.crazytunnel.connection;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class MessageReader
{
	private final ByteArrayInputStream byteArray;
	private final DataInputStream dataStream;
	
	public MessageReader(byte[] message)
	{
		this.byteArray = new ByteArrayInputStream(message);
		this.dataStream = new DataInputStream(this.byteArray);
	}
	
	public boolean getBoolean()
	{
		try
		{
			return this.dataStream.readBoolean();
		}
		catch (IOException e)
		{
		}
		
		return false;
	}
	
	public byte getByte()
	{
		try
		{
			return this.dataStream.readByte();
		}
		catch (IOException e)
		{
		}
		
		return 0;
	}
	
	public int getInt()
	{
		try
		{
			return this.dataStream.readInt();
		}
		catch (IOException e)
		{
		}
		
		return 0;
	}
	
	public float getFloat()
	{
		try
		{
			return this.dataStream.readFloat();
		}
		catch (IOException e)
		{
		}
		
		return 0;
	}
	
	public String getString()
	{
		try
		{
			return this.dataStream.readUTF();
		}
		catch (IOException e)
		{
		}
		
		return "";
	}
}