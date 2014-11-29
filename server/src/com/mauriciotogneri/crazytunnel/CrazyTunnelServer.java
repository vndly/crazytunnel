package com.mauriciotogneri.crazytunnel;

import com.mauriciotogneri.crazytunnel.connection.Game;

public class CrazyTunnelServer
{
	public static void main(String[] args)
	{
		try
		{
			Game game = new Game(7777, 2, 3);
			game.start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}