package com.mauriciotogneri.crazytunnel.objects;

import android.os.Vibrator;
import com.mauriciotogneri.crazytunnel.engine.Camera;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.engine.Sprite;
import com.mauriciotogneri.crazytunnel.input.InputEvent;
import com.mauriciotogneri.crazytunnel.shapes.Shape;
import com.mauriciotogneri.crazytunnel.shapes.Square;

public class PlayerBox
{
	private final Camera camera;
	private final Level level;
	private final Vibrator vibrator;
	
	private final Sprite sprite;
	private boolean vibrating = false;
	
	private float acceleration = 0;
	
	private static final float SLOW_RATIO = 0.2f; // 0.1f;
	
	private static final float GRAVITY = 1 * PlayerBox.SLOW_RATIO;
	private static final float JUMP_FORCE = 3;
	
	private static final float SPEED = 40 * PlayerBox.SLOW_RATIO;
	
	private static final float MAX_ACCELERATION_UP = 30 * PlayerBox.SLOW_RATIO;
	private static final float MAX_ACCELERATION_DOWN = 30 * PlayerBox.SLOW_RATIO;
	
	private static final int SIZE = 5;
	
	public PlayerBox(Camera camera, Level level, Vibrator vibrator, float x, float y, int color)
	{
		this.camera = camera;
		this.level = level;
		this.vibrator = vibrator;
		
		Shape square = new Square(PlayerBox.SIZE, color);
		this.sprite = new Sprite(square, x, y);
	}
	
	public void update(float delta, InputEvent input)
	{
		if (!this.level.finished(this.sprite))
		{
			if (input.jump)
			{
				this.acceleration += PlayerBox.JUMP_FORCE;
			}
			
			this.acceleration -= PlayerBox.GRAVITY;
			
			if (this.acceleration > PlayerBox.MAX_ACCELERATION_UP)
			{
				this.acceleration = PlayerBox.MAX_ACCELERATION_UP;
			}
			else if (this.acceleration < -PlayerBox.MAX_ACCELERATION_DOWN)
			{
				this.acceleration = -PlayerBox.MAX_ACCELERATION_DOWN;
			}
			
			this.sprite.x += delta * getSpeed();
			this.sprite.y += delta * this.acceleration;
			
			if (this.sprite.y < 0)
			{
				this.sprite.y = 0;
			}
			else if (this.sprite.y > (Renderer.RESOLUTION_Y - PlayerBox.SIZE))
			{
				this.sprite.y = Renderer.RESOLUTION_Y - PlayerBox.SIZE;
			}
		}
	}
	
	public float getX()
	{
		return this.sprite.x;
	}
	
	public float getY()
	{
		return this.sprite.y;
	}
	
	private float getSpeed()
	{
		float result = PlayerBox.SPEED;
		
		if (this.level.collide(this.sprite))
		{
			result *= 0.5f;
			
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
		
		return result;
	}
	
	public void pause()
	{
		this.vibrating = false;
		this.vibrator.cancel();
	}
	
	public void render(Renderer renderer)
	{
		if (this.camera.isInside(this.sprite))
		{
			this.sprite.render(renderer);
		}
	}
}