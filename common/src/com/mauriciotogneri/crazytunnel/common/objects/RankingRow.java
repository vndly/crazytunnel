package com.mauriciotogneri.crazytunnel.common.objects;

public class RankingRow
{
	public final String playerName;
	public final int playerColor;
	public final double time;
	public double timeDifference = 0;
	
	public RankingRow(String playerName, int playerColor, double time, double timeDifference)
	{
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.time = time;
		this.timeDifference = timeDifference;
	}
	
	public RankingRow(String playerName, int playerColor, double time)
	{
		this(playerName, playerColor, time, 0);
	}
}