package com.mauriciotogneri.crazytunnel.connection;

import java.util.ArrayList;
import java.util.List;
import com.mauriciotogneri.crazytunnel.objects.Player;

public class Messages
{
	public static class SetPlayerColor
	{
		public final int color;
		
		public static final byte CODE = 1;
		
		public SetPlayerColor(MessageReader reader)
		{
			this.color = reader.getInt();
		}
		
		public static byte[] create(int color)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetPlayerColor.CODE);
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
	
	public static class SetRegisteredPlayers
	{
		public final Player[] players;
		
		public static final byte CODE = 3;
		
		public SetRegisteredPlayers(MessageReader reader)
		{
			this.players = new Player[reader.getInt()];
			
			for (int i = 0; i < this.players.length; i++)
			{
				String macAddress = reader.getString();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players[i] = new Player(macAddress, name, color);
			}
		}
		
		public static byte[] create(List<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetRegisteredPlayers.CODE);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putString(player.macAddress);
				writer.putString(player.name);
				writer.putInt(player.color);
			}
			
			return writer.getMessage();
		}
	}
	
	public static class SetFinalPlayersList
	{
		public final List<Player> players;
		
		public static final byte CODE = 4;
		
		public SetFinalPlayersList(MessageReader reader)
		{
			this.players = new ArrayList<Player>();
			
			int length = reader.getInt();
			
			for (int i = 0; i < length; i++)
			{
				String macAddress = reader.getString();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players.add(new Player(macAddress, name, color));
			}
		}
		
		public static byte[] create(List<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetFinalPlayersList.CODE);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putString(player.macAddress);
				writer.putString(player.name);
				writer.putInt(player.color);
			}
			
			return writer.getMessage();
		}
	}
}