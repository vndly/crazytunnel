package com.mauriciotogneri.crazytunnel.common.messages;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MessageWriter
{
	private final ByteArrayOutputStream byteArray;
	private final DataOutputStream dataStream;
	
	public MessageWriter()
	{
		this.byteArray = new ByteArrayOutputStream();
		this.dataStream = new DataOutputStream(this.byteArray);
	}
	
	public void putBoolean(boolean value)
	{
		try
		{
			this.dataStream.writeBoolean(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public void putByte(byte value)
	{
		try
		{
			this.dataStream.writeByte(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public void putInt(int value)
	{
		try
		{
			this.dataStream.writeInt(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public void putFloat(float value)
	{
		try
		{
			this.dataStream.writeFloat(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public void putDouble(double value)
	{
		try
		{
			this.dataStream.writeDouble(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public void putString(String value)
	{
		try
		{
			this.dataStream.writeUTF(value);
		}
		catch (IOException e)
		{
		}
	}
	
	public byte[] getMessage()
	{
		try
		{
			this.dataStream.flush();
			this.dataStream.close();
		}
		catch (IOException e)
		{
		}
		
		return this.byteArray.toByteArray();
	}
}