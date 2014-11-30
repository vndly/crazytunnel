package com.mauriciotogneri.crazytunnel.client.objects.box;

import com.mauriciotogneri.crazytunnel.client.engine.Camera;
import com.mauriciotogneri.crazytunnel.client.objects.level.Level;

public class EnemyBox extends Box
{
	private boolean jumping;
	
	public EnemyBox(Camera camera, Level level, float x, float y, int color)
	{
		super(camera, level, x, y, color);
	}
	
	public void update(double delta)
	{
		if (!finished())
		{
			if (this.jumping)
			{
				jump();
			}
			
			updatePosition(delta);
		}
	}
	
	@Override
	public void restart()
	{
		super.restart();
		
		this.jumping = false;
	}
	
	public void update(float x, float y, boolean jumping)
	{
		this.jumping = jumping;
		
		updatePosition(x, y);
	}
}