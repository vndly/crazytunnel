package com.mauriciotogneri.crazytunnel.screens.game;

public interface GameEvent
{
	void onReceive(byte[] message);
	
	// when another player is disconnected
	void playerDisconnect(String macAddress);
	
	// when the own player is disconnected
	void onDisconnect();
}