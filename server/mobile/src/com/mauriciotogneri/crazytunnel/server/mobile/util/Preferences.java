package com.mauriciotogneri.crazytunnel.server.mobile.util;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
	private static final String PREFERENCES = "PREFERENCES";
	private static final String ATTRIBUTE_PLAYERS = "PLAYERS";
	private static final String ATTRIBUTE_LAPS = "LAPS";
	private static final String ATTRIBUTE_PORT = "PORT";
	
	private static Context context;
	
	public static void initialize(Context context)
	{
		Preferences.context = context;
	}
	
	public static void setPlayers(int players)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putInt(Preferences.ATTRIBUTE_PLAYERS, players);
		editor.commit();
	}
	
	public static int getPlayers()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getInt(Preferences.ATTRIBUTE_PLAYERS, 0);
	}
	
	public static void setLaps(int laps)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putInt(Preferences.ATTRIBUTE_LAPS, laps);
		editor.commit();
	}
	
	public static int getLaps()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getInt(Preferences.ATTRIBUTE_LAPS, 0);
	}
	
	public static void setPort(int port)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putInt(Preferences.ATTRIBUTE_PORT, port);
		editor.commit();
	}
	
	public static int getPort()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getInt(Preferences.ATTRIBUTE_PORT, 0);
	}
}