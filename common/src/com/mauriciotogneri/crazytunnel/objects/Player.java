package com.mauriciotogneri.crazytunnel.objects;

public class Player
{
	public final byte id;
	public final String name;
	public final int color;
	
	public Player(byte id, String name, int color)
	{
		this.id = id;
		this.name = name;
		this.color = color;
	}
}