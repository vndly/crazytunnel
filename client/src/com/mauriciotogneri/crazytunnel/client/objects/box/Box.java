package com.mauriciotogneri.crazytunnel.client.objects.box;

import com.mauriciotogneri.crazytunnel.client.engine.Camera;
import com.mauriciotogneri.crazytunnel.client.engine.Renderer;
import com.mauriciotogneri.crazytunnel.client.engine.Sprite;
import com.mauriciotogneri.crazytunnel.client.objects.level.Level;
import com.mauriciotogneri.crazytunnel.client.shapes.Shape;
import com.mauriciotogneri.crazytunnel.client.shapes.Square;

public class Box
{
	private final Camera camera;
	private final Level level;
	
	private final float initialX;
	private final float initialY;
	
	private final Sprite sprite;
	private float acceleration = 0;
	
	private final Object lock = new Object();
	
	private static final float SLOW_RATIO = 1f; // 0.2f;
	
	private static final float GRAVITY = 1 * Box.SLOW_RATIO;
	protected static final float JUMP_FORCE = 3;
	
	private static final float SPEED = 30 * Box.SLOW_RATIO;
	
	private static final float MAX_ACCELERATION_UP = 30 * Box.SLOW_RATIO;
	private static final float MAX_ACCELERATION_DOWN = 30 * Box.SLOW_RATIO;
	
	private static final int SIZE = 5;
	
	public Box(Camera camera, Level level, float x, float y, int color)
	{
		this.camera = camera;
		this.level = level;
		
		this.initialX = x;
		this.initialY = y;
		
		Shape square = new Square(Box.SIZE, color);
		this.sprite = new Sprite(square, x, y);
	}
	
	public void restart()
	{
		this.sprite.x = this.initialX;
		this.sprite.y = this.initialY;
	}
	
	protected void jump()
	{
		this.acceleration += Box.JUMP_FORCE;
	}
	
	protected void updatePosition(float delta)
	{
		this.acceleration -= Box.GRAVITY;
		
		if (this.acceleration > Box.MAX_ACCELERATION_UP)
		{
			this.acceleration = Box.MAX_ACCELERATION_UP;
		}
		else if (this.acceleration < -Box.MAX_ACCELERATION_DOWN)
		{
			this.acceleration = -Box.MAX_ACCELERATION_DOWN;
		}
		
		synchronized (this.lock)
		{
			this.sprite.x += delta * getSpeed();
			this.sprite.y += delta * this.acceleration;
			
			if (this.sprite.y < 0)
			{
				this.sprite.y = 0;
			}
			else if (this.sprite.y > (Renderer.RESOLUTION_Y - Box.SIZE))
			{
				this.sprite.y = Renderer.RESOLUTION_Y - Box.SIZE;
			}
		}
	}
	
	public boolean finished()
	{
		return this.level.finished(this.sprite);
	}
	
	public float getX()
	{
		synchronized (this.lock)
		{
			return this.sprite.x;
		}
	}
	
	public float getY()
	{
		synchronized (this.lock)
		{
			return this.sprite.y;
		}
	}
	
	protected void updatePosition(float x, float y)
	{
		synchronized (this.lock)
		{
			if (x > this.sprite.x)
			{
				this.sprite.x = x;
				this.sprite.y = y;
			}
		}
	}
	
	protected boolean collide()
	{
		return (this.level.collide(this.sprite));
	}
	
	private float getSpeed()
	{
		float result = Box.SPEED;
		
		if (collide())
		{
			result *= 0.5f;
		}
		
		return result;
	}
	
	public void render(Renderer renderer)
	{
		synchronized (this.lock)
		{
			if (this.camera.isInside(this.sprite))
			{
				this.sprite.render(renderer);
			}
		}
	}
}