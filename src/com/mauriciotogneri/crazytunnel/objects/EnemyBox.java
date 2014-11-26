package com.mauriciotogneri.crazytunnel.objects;

import com.mauriciotogneri.crazytunnel.engine.Camera;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.engine.Sprite;
import com.mauriciotogneri.crazytunnel.shapes.Shape;
import com.mauriciotogneri.crazytunnel.shapes.Square;

public class EnemyBox
{
	private boolean jumping;
	
	private final Camera camera;
	private final Level level;
	private final Sprite sprite;
	
	private float acceleration = 0;
	
	private static final float SLOW_RATIO = 0.2f; // 0.1f;
	
	private static final float GRAVITY = 1 * EnemyBox.SLOW_RATIO;
	private static final float JUMP_FORCE = 3;
	
	private static final float SPEED = 40 * EnemyBox.SLOW_RATIO;
	
	private static final float MAX_ACCELERATION_UP = 30 * EnemyBox.SLOW_RATIO;
	private static final float MAX_ACCELERATION_DOWN = 30 * EnemyBox.SLOW_RATIO;
	
	private static final int SIZE = 5;
	
	public EnemyBox(Camera camera, Level level, float x, float y, int color)
	{
		this.camera = camera;
		this.level = level;
		
		Shape square = new Square(EnemyBox.SIZE, color);
		this.sprite = new Sprite(square, x, y);
	}
	
	public void update(float delta)
	{
		if (!this.level.finished(this.sprite))
		{
			if (this.jumping)
			{
				this.acceleration += EnemyBox.JUMP_FORCE;
			}
			
			this.acceleration -= EnemyBox.GRAVITY;
			
			if (this.acceleration > EnemyBox.MAX_ACCELERATION_UP)
			{
				this.acceleration = EnemyBox.MAX_ACCELERATION_UP;
			}
			else if (this.acceleration < -EnemyBox.MAX_ACCELERATION_DOWN)
			{
				this.acceleration = -EnemyBox.MAX_ACCELERATION_DOWN;
			}
			
			this.sprite.x += delta * getSpeed();
			this.sprite.y += delta * this.acceleration;
			
			if (this.sprite.y < 0)
			{
				this.sprite.y = 0;
			}
			else if (this.sprite.y > (Renderer.RESOLUTION_Y - EnemyBox.SIZE))
			{
				this.sprite.y = Renderer.RESOLUTION_Y - EnemyBox.SIZE;
			}
		}
	}
	
	private float getSpeed()
	{
		float result = EnemyBox.SPEED;
		
		if (this.level.collide(this.sprite))
		{
			result *= 0.5f;
		}
		
		return result;
	}
	
	public void update(float x, float y, boolean jumping)
	{
		this.sprite.x = x;
		this.sprite.y = y;
		this.jumping = jumping;
	}
	
	public void render(Renderer renderer)
	{
		if (this.camera.isInside(this.sprite))
		{
			this.sprite.render(renderer);
		}
	}
}