package com.mauriciotogneri.crazytunnel.objects;

import android.bluetooth.BluetoothDevice;

public class Player
{
	public final BluetoothDevice device;
	public String name = "";
	public int color = 0;
	
	public Player(BluetoothDevice device)
	{
		this.device = device;
	}
}