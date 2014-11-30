package com.mauriciotogneri.crazytunnel.common.objects;

public class Color
{
	public static int getColor(int r, int g, int b, int a)
	{
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}
}