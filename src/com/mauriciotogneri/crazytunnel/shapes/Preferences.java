package com.mauriciotogneri.crazytunnel.shapes;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences
{
	private static final String PREFERENCES = "PREFERENCES";
	private static final String ATTRIBUTE_PLAYER_NAME = "PLAYER_NAME";
	
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
}