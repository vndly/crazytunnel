package com.mauriciotogneri.crazytunnel.shapes;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
	private static final String PREFERENCES = "PREFERENCES";
	private static final String ATTRIBUTE_PLAYER_NAME = "PLAYER_NAME";
	private static final String ATTRIBUTE_SERVER_IP = "SERVER_IP";
	private static final String ATTRIBUTE_SERVER_PORT = "SERVER_PORT";
	
	private static Context context;
	
	public static void initialize(Context context)
	{
		Preferences.context = context;
	}
	
	public static void setPlayerName(String name)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putString(Preferences.ATTRIBUTE_PLAYER_NAME, name);
		editor.commit();
	}
	
	public static String getPlayerName()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getString(Preferences.ATTRIBUTE_PLAYER_NAME, "");
	}
	
	public static void setServerIP(String ip)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putString(Preferences.ATTRIBUTE_SERVER_IP, ip);
		editor.commit();
	}
	
	public static String getServerIP()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getString(Preferences.ATTRIBUTE_SERVER_IP, "");
	}
	
	public static void setServerPort(int port)
	{
		SharedPreferences.Editor editor = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE).edit();
		editor.putInt(Preferences.ATTRIBUTE_SERVER_PORT, port);
		editor.commit();
	}
	
	public static int getServerPort()
	{
		SharedPreferences preferences = Preferences.context.getSharedPreferences(Preferences.PREFERENCES, Context.MODE_PRIVATE);
		
		return preferences.getInt(Preferences.ATTRIBUTE_SERVER_PORT, 0);
	}
}