package com.mauriciotogneri.crazytunnel.connection;

public class Messages
{
	public static class SetFreeColor
	{
		public final int color;
		
		public static final byte CODE = 1;
		
		public SetFreeColor(MessageReader reader)
		{
			this.color = reader.getInt();
		}
		
		public static byte[] create(int color)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetFreeColor.CODE);
			writer.putInt(color);
			
			return writer.getMessage();
		}
	}
	
	public static class SetPlayerName
	{
		public final String name;
		public final int color;
		
		public static final byte CODE = 2;
		
		public SetPlayerName(MessageReader reader)
		{
			this.name = reader.getString();
			this.color = reader.getInt();
		}
		
		public static byte[] create(String name, int color)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetPlayerName.CODE);
			writer.putString(name);
			writer.putInt(color);
			
			return writer.getMessage();
		}
	}
}