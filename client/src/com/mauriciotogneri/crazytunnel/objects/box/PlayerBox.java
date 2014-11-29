package com.mauriciotogneri.crazytunnel.objects.box;

import android.os.Vibrator;
import com.mauriciotogneri.crazytunnel.engine.Camera;
import com.mauriciotogneri.crazytunnel.input.InputEvent;
import com.mauriciotogneri.crazytunnel.objects.level.Level;

public class PlayerBox extends Box
{
	private final Vibrator vibrator;
	
	private boolean vibrating = false;
	
	public PlayerBox(Camera camera, Level level, Vibrator vibrator, float x, float y, int color)
	{
		super(camera, level, x, y, color);
		
		this.vibrator = vibrator;
	}
	
	public void update(float delta, InputEvent input)
	{
		if (!finished())
		{
			if (input.jump)
			{
				this.acceleration += Box.JUMP_FORCE;
			}
			
			updatePosition(delta);
			
			if (collide())
			{
				if (!this.vibrating)
				{
					this.vibrator.vibrate(10000);
					this.vibrating = true;
				}
			}
			else
			{
				this.vibrating = false;
				this.vibrator.cancel();
			}
		}
	}
	
	@Override
	public void restart()
	{
		super.restart();
		
		pause();
	}
	
	public void pause()
	{
		this.vibrating = false;
		this.vibrator.cancel();
	}
}