package com.mauriciotogneri.crazytunnel.objects;

import com.mauriciotogneri.crazytunnel.engine.Camera;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.engine.Sprite;

public class Level
{
	private final Camera camera;
	private final LevelDefinition levelDefinition;
	
	public Level(Camera camera, LevelDefinition levelDefinition)
	{
		this.camera = camera;
		this.levelDefinition = levelDefinition;
	}
	
	public boolean finished(Sprite sprite)
	{
		return this.levelDefinition.finished(sprite);
	}
	
	public boolean collide(Sprite sprite)
	{
		boolean result = false;
		
		for (Sprite obstacle : this.levelDefinition.getSprites())
		{
			if (obstacle.collide(sprite))
			{
				result = true;
				break;
			}
		}
		
		return result;
	}
	
	public void render(Renderer renderer)
	{
		for (Sprite sprite : this.levelDefinition.getSprites())
		{
			if (this.camera.isInside(sprite))
			{
				sprite.render(renderer);
			}
		}
	}
}