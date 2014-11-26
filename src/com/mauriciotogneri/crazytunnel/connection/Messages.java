package com.mauriciotogneri.crazytunnel.connection;

import java.util.ArrayList;
import java.util.List;
import com.mauriciotogneri.crazytunnel.objects.Player;
import com.mauriciotogneri.crazytunnel.objects.box.PlayerBox;

public class Messages
{
	public static class SetPlayerInfo
	{
		public final byte id;
		public final int color;
		
		public static final byte CODE = 1;
		
		public SetPlayerInfo(MessageReader reader)
		{
			this.id = reader.getByte();
			this.color = reader.getInt();
		}
		
		public static byte[] create(byte id, int color)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetPlayerInfo.CODE);
			writer.putByte(id);
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
				byte id = reader.getByte();
				String macAddress = reader.getString();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players[i] = new Player(id, macAddress, name, color);
			}
		}
		
		public static byte[] create(List<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetRegisteredPlayers.CODE);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putByte(player.id);
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
				byte id = reader.getByte();
				String macAddress = reader.getString();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players.add(new Player(id, macAddress, name, color));
			}
		}
		
		public static byte[] create(List<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetFinalPlayersList.CODE);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putByte(player.id);
				writer.putString(player.macAddress);
				writer.putString(player.name);
				writer.putInt(player.color);
			}
			
			return writer.getMessage();
		}
	}
	
	public static class StartRace
	{
		public static final byte CODE = 5;
		
		public StartRace()
		{
		}
		
		public static byte[] create()
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(StartRace.CODE);
			
			return writer.getMessage();
		}
	}
	
	public static class RestartRace
	{
		public static final byte CODE = 6;
		
		public RestartRace()
		{
		}
		
		public static byte[] create()
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(RestartRace.CODE);
			
			return writer.getMessage();
		}
	}
	
	public static class SetPlayerBoxPosition
	{
		public final byte playerId;
		public final boolean jumping;
		public final float x;
		public final float y;
		
		public static final byte CODE = 7;
		
		public SetPlayerBoxPosition(MessageReader reader)
		{
			this.playerId = reader.getByte();
			this.jumping = reader.getBoolean();
			this.x = reader.getFloat();
			this.y = reader.getFloat();
		}
		
		public byte[] create()
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetPlayerBoxPosition.CODE);
			writer.putByte(this.playerId);
			writer.putFloat(this.x);
			writer.putFloat(this.y);
			
			return writer.getMessage();
		}
		
		public static byte[] create(Player player, PlayerBox box, boolean jumping)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(SetPlayerBoxPosition.CODE);
			writer.putByte(player.id);
			writer.putBoolean(jumping);
			writer.putFloat(box.getX());
			writer.putFloat(box.getY());
			
			return writer.getMessage();
		}
	}
}