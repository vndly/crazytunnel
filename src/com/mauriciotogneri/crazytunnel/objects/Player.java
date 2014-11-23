package com.mauriciotogneri.crazytunnel.objects;

public class Player
{
	public String macAddress;
	public String name;
	public int color = 0;
	
	public Player(String macAddress, String name)
	{
		this.macAddress = macAddress;
		this.name = name;
	}
}