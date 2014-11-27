package com.mauriciotogneri.crazytunnel.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.InputStream;
import java.io.InputStreamReader;
import android.content.Context;

public class FileUtils
{
	public static String readTextFile(Context context, int resourceId)
	{
		StringBuilder builder = new StringBuilder();
		
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader bufferedReader = null;
		
		try
		{
			inputStream = context.getResources().openRawResource(resourceId);
			inputStreamReader = new InputStreamReader(inputStream);
			bufferedReader = new BufferedReader(inputStreamReader);
			
			String nextLine;
			
			while ((nextLine = bufferedReader.readLine()) != null)
			{
				builder.append(nextLine);
				builder.append('\n');
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			FileUtils.closeResource(inputStream);
			FileUtils.closeResource(inputStreamReader);
			FileUtils.closeResource(bufferedReader);
		}
		
		return builder.toString();
	}
	
	public static InputStream getInputStream(Context context, int resourceId)
	{
		InputStream result = null;
		
		try
		{
			result = context.getResources().openRawResource(resourceId);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static void closeResource(Closeable resource)
	{
		if (resource != null)
		{
			try
			{
				resource.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}