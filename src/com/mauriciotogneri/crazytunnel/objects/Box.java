package com.mauriciotogneri.crazytunnel.objects;

import android.graphics.Color;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.shapes.Shape;
import com.mauriciotogneri.crazytunnel.shapes.Square;

public class Box
{
	private float x = 0;
	private float y = 0;
	private final Shape shape;
	
	public Box(float x, float y)
	{
		this.x = x;
		this.y = y;
		
		this.shape = new Square(50, Color.RED);
	}
	
	public boolean collide(PlayerBox playerBox)
	{
		return false; // GeometryUtils.collide(this.sprite, player.getSprite());
	}
	
	public void update(float delta, float distance, float gameSpeed)
	{
		// this.shape.moveX(-distance);
		
		// if (this.sprite.getRight() < 0)
		// {
		// finish();
		// }
	}
	
	public void render(Renderer renderer)
	{
		this.shape.render(renderer, this.x, this.y);
	}
}