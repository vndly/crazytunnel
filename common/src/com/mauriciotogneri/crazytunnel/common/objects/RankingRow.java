package com.mauriciotogneri.crazytunnel.common.objects;

public class RankingRow
{
	public final String playerName;
	public final int playerColor;
	public final float time;
	
	public RankingRow(String playerName, int playerColor, float time)
	{
		this.playerName = playerName;
		this.playerColor = playerColor;
		this.time = time;
	}
}