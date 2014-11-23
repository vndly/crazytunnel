package com.mauriciotogneri.crazytunnel.objects;

import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.engine.Sprite;
import com.mauriciotogneri.crazytunnel.shapes.Shape;
import com.mauriciotogneri.crazytunnel.shapes.Square;

public class EnemyBox
{
	private final Sprite sprite;
	
	private static final int SIZE = 5;
	
	public EnemyBox(float x, float y, int color)
	{
		Shape square = new Square(EnemyBox.SIZE, color);
		this.sprite = new Sprite(square, x, y);
	}
	
	public void update(float x, float y)
	{
		this.sprite.x = x;
		this.sprite.y = y;
	}
	
	public void render(Renderer renderer)
	{
		this.sprite.render(renderer);
	}
}