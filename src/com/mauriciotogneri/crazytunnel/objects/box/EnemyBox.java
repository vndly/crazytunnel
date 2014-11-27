package com.mauriciotogneri.crazytunnel.objects.box;

import com.mauriciotogneri.crazytunnel.engine.Camera;
import com.mauriciotogneri.crazytunnel.objects.level.Level;

public class EnemyBox extends Box
{
	private boolean jumping;
	
	public EnemyBox(Camera camera, Level level, float x, float y, int color)
	{
		super(camera, level, x, y, color);
	}
	
	public void update(float delta)
	{
		if (!finished())
		{
			if (this.jumping)
			{
				this.acceleration += Box.JUMP_FORCE;
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
		this.sprite.x = x;
		this.sprite.y = y;
		this.jumping = jumping;
	}
}