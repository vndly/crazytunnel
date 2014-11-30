package com.mauriciotogneri.crazytunnel.common.objects;

public class RankingRow
{
	public final String playerName;
	public final int playerColor;
	public final float time;
	public float timeDifference = 0;
	
	public RankingRow(String playerName, int playerColor, float time, float timeDifference)
	{
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.time = time;
		this.timeDifference = timeDifference;
	}
	
	public RankingRow(String playerName, int playerColor, float time)
	{
		this(playerName, playerColor, time, 0);
	}
}