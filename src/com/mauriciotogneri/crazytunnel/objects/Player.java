package com.mauriciotogneri.crazytunnel.objects;

public class Player
{
	public byte id = 0;
	public String name = "";
	public int color = 0;
	
	public Player()
	{
	}
	
	public Player(byte id)
	{
		this.id = id;
	}
	
	public Player(byte id, String name, int color)
	{
		this.id = id;
		this.name = name;
		this.color = color;
	}
	
	public boolean isValid()
	{
		return (!this.name.isEmpty()) && (this.color != 0);
	}
}