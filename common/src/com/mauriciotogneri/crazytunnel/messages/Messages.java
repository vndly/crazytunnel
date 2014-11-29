package com.mauriciotogneri.crazytunnel.messages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import com.mauriciotogneri.crazytunnel.objects.Player;

public class Messages
{
	public static class PlayerConnect
	{
		public final String name;
		public final int udpPort;
		
		public static final byte CODE = 1;
		
		public PlayerConnect(MessageReader reader)
		{
			this.name = reader.getString();
			this.udpPort = reader.getInt();
		}
		
		public static byte[] create(String name, int udpPort)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(PlayerConnect.CODE);
			writer.putString(name);
			writer.putInt(udpPort);
			
			return writer.getMessage();
		}
	}
	
	public static class PlayerInfo
	{
		public final byte id;
		public final String name;
		public final int color;
		public final int udpPort;
		
		public static final byte CODE = 2;
		
		public PlayerInfo(MessageReader reader)
		{
			this.id = reader.getByte();
			this.name = reader.getString();
			this.color = reader.getInt();
			this.udpPort = reader.getInt();
		}
		
		public static byte[] create(byte id, String name, int color, int udpPort)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(PlayerInfo.CODE);
			writer.putByte(id);
			writer.putString(name);
			writer.putInt(color);
			writer.putInt(udpPort);
			
			return writer.getMessage();
		}
	}
	
	public static class PlayersList
	{
		public final Player[] players;
		
		public static final byte CODE = 3;
		
		public PlayersList(MessageReader reader)
		{
			this.players = new Player[reader.getInt()];
			
			for (int i = 0; i < this.players.length; i++)
			{
				byte id = reader.getByte();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players[i] = new Player(id, name, color);
			}
		}
		
		public static byte[] create(Collection<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(PlayersList.CODE);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putByte(player.id);
				writer.putString(player.name);
				writer.putInt(player.color);
			}
			
			return writer.getMessage();
		}
	}
	
	public static class StartGame
	{
		public final int laps;
		public final List<Player> players;
		
		public static final byte CODE = 4;
		
		public StartGame(MessageReader reader)
		{
			this.laps = reader.getInt();
			this.players = new ArrayList<Player>();
			
			int length = reader.getInt();
			
			for (int i = 0; i < length; i++)
			{
				byte id = reader.getByte();
				String name = reader.getString();
				int color = reader.getInt();
				
				this.players.add(new Player(id, name, color));
			}
		}
		
		public static byte[] create(int laps, Collection<Player> players)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(StartGame.CODE);
			writer.putInt(laps);
			writer.putInt(players.size());
			
			for (Player player : players)
			{
				writer.putByte(player.id);
				writer.putString(player.name);
				writer.putInt(player.color);
			}
			
			return writer.getMessage();
		}
	}
	
	public static class Ready
	{
		public static final byte CODE = 5;
		
		public Ready()
		{
		}
		
		public static byte[] create()
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(Ready.CODE);
			
			return writer.getMessage();
		}
	}
	
	public static class StartRace
	{
		public static final byte CODE = 6;
		
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
		public static final byte CODE = 7;
		
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
	
	public static class PlayerBoxPosition
	{
		public final byte playerId;
		public final boolean jumping;
		public final float x;
		public final float y;
		
		public static final byte CODE = 8;
		
		public PlayerBoxPosition(MessageReader reader)
		{
			this.playerId = reader.getByte();
			this.jumping = reader.getBoolean();
			this.x = reader.getFloat();
			this.y = reader.getFloat();
		}
		
		public byte[] create()
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(PlayerBoxPosition.CODE);
			writer.putByte(this.playerId);
			writer.putBoolean(this.jumping);
			writer.putFloat(this.x);
			writer.putFloat(this.y);
			
			return writer.getMessage();
		}
		
		public static byte[] create(byte playerId, float x, float y, boolean jumping)
		{
			MessageWriter writer = new MessageWriter();
			writer.putByte(PlayerBoxPosition.CODE);
			writer.putByte(playerId);
			writer.putBoolean(jumping);
			writer.putFloat(x);
			writer.putFloat(y);
			
			return writer.getMessage();
		}
	}
}