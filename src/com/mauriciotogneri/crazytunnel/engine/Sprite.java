package com.mauriciotogneri.crazytunnel.engine;

import com.mauriciotogneri.crazytunnel.shapes.Shape;

public class Sprite
{
	public float x = 0;
	public float y = 0;
	public final float width;
	public final float height;
	public final Shape shape;
	
	public Sprite(Shape shape, float x, float y)
	{
		this.x = x;
		this.y = y;
		
		this.shape = shape;
		
		this.width = shape.getWidth();
		this.height = shape.getHeight();
	}
	
	public void render(Renderer renderer)
	{
		this.shape.render(renderer, this.x, this.y);
	}
	
	public Sprite copyAt(float x, float y)
	{
		return new Sprite(this.shape, x, y);
	}
	
	public boolean collide(Sprite sprite)
	{
		float xA = this.x;
		float yA = this.y;
		float widthA = this.width;
		float heightA = this.height;
		
		float xB = sprite.x;
		float yB = sprite.y;
		float widthB = sprite.width;
		float heightB = sprite.height;
		
		boolean onLeft = ((xA + widthA) < xB);
		boolean onRight = ((xB + widthB) < xA);
		boolean onTop = ((yB + heightB) < yA);
		boolean onBottom = ((yA + heightA) < yB);
		
		return (!(onLeft || onRight || onTop || onBottom));
	}
}