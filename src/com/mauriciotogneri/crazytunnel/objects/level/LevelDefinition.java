package com.mauriciotogneri.crazytunnel.objects.level;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.engine.Sprite;
import com.mauriciotogneri.crazytunnel.shapes.Rectangle;
import com.mauriciotogneri.crazytunnel.shapes.Shape;

public class LevelDefinition
{
	private final int length;
	private final int laps;
	
	private final List<Sprite> base = new ArrayList<Sprite>();
	private final List<Sprite> collisionableSprites = new ArrayList<Sprite>();
	private final List<Sprite> nonCollisionableSprites = new ArrayList<Sprite>();
	
	public static final int WALL_COLOR = Color.argb(255, 90, 110, 120);
	
	private static final int WALL_HEIGHT = 5;
	
	public LevelDefinition(int length, int laps)
	{
		this.length = length;
		this.laps = laps;
		
		Shape wall = new Rectangle(Renderer.RESOLUTION_X * (laps + 2), LevelDefinition.WALL_HEIGHT, LevelDefinition.WALL_COLOR);
		Sprite wallBottom = new Sprite(wall, -Renderer.RESOLUTION_X, 0);
		Sprite wallTop = new Sprite(wall, -Renderer.RESOLUTION_X, Renderer.RESOLUTION_Y - LevelDefinition.WALL_HEIGHT);
		
		add(wallTop);
		add(wallBottom);
	}
	
	public void add(Sprite sprite)
	{
		this.base.add(sprite);
	}
	
	public List<Sprite> getCollisionableSprites()
	{
		return this.collisionableSprites;
	}
	
	public List<Sprite> getNonCollisionableSprites()
	{
		return this.nonCollisionableSprites;
	}
	
	public boolean finished(Sprite sprite)
	{
		return (sprite.x > (this.length * this.laps));
	}
	
	public void build()
	{
		for (int i = 0; i < this.laps; i++)
		{
			Shape startLineShape = new Rectangle(1, Renderer.RESOLUTION_Y, Color.argb(255, 200, 200, 200));
			Sprite startLine = new Sprite(startLineShape, this.length * i, 0);
			this.nonCollisionableSprites.add(startLine);
			
			for (Sprite sprite : this.base)
			{
				this.collisionableSprites.add(sprite.copyAt(sprite.x + (this.length * i), sprite.y));
			}
		}
		
		Shape lastStartLineShape = new Rectangle(1, Renderer.RESOLUTION_Y, Color.argb(255, 200, 200, 200));
		Sprite lastStartLine = new Sprite(lastStartLineShape, this.length * this.laps, 0);
		this.nonCollisionableSprites.add(lastStartLine);
	}
}