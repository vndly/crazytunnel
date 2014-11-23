package com.mauriciotogneri.crazytunnel.objects;

import java.util.ArrayList;
import java.util.List;
import com.mauriciotogneri.crazytunnel.engine.Sprite;

public class LevelDefinition
{
	private final int length;
	private final int laps;
	private final List<Sprite> sprites = new ArrayList<Sprite>();
	
	public LevelDefinition(int length, int laps)
	{
		this.length = length;
		this.laps = laps;
	}
	
	public void add(Sprite sprite)
	{
		this.sprites.add(sprite);
	}
	
	public List<Sprite> getSprites()
	{
		return this.sprites;
	}
	
	public boolean finished(Sprite sprite)
	{
		return (sprite.x > (this.length * this.laps));
	}
	
	public void build()
	{
		List<Sprite> base = new ArrayList<Sprite>();
		
		for (Sprite sprite : this.sprites)
		{
			base.add(sprite);
		}
		
		for (int i = 1; i < this.laps; i++)
		{
			for (Sprite sprite : base)
			{
				this.sprites.add(sprite.copyAt(sprite.x + (this.length * i), sprite.y));
			}
		}
	}
}