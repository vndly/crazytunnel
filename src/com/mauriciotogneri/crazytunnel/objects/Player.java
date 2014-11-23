package com.mauriciotogneri.crazytunnel.objects;

public class Player
{
	public final String macAddress;
	public String name = "";
	public int color = 0;
	
	public Player(String macAddress)
	{
		this.macAddress = macAddress;
	}
	
	public Player(String macAddress, String name, int color)
	{
		this.macAddress = macAddress;
		this.name = name;
		this.color = color;
	}
	
	public boolean isValid()
	{
		return (!this.name.isEmpty()) && (this.color != 0);
	}
}