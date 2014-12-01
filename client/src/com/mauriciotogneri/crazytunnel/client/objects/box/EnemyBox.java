package com.mauriciotogneri.crazytunnel.client.objects.box;

import com.mauriciotogneri.crazytunnel.client.engine.Camera;
import com.mauriciotogneri.crazytunnel.client.objects.level.Level;

public class EnemyBox extends Box
{
	public EnemyBox(Camera camera, Level level, float x, float y, int color)
	{
		super(camera, level, x, y, color);
	}
	
	public void update(double delta)
	{
		if (!finished())
		{
			updatePosition(delta);
		}
	}
	
	public void update(float x, float y, boolean jumping)
	{
		updatePosition(x, y);
		updateJump(jumping);
	}
}