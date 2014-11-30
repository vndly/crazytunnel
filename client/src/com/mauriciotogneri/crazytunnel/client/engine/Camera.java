package com.mauriciotogneri.crazytunnel.client.engine;

public class Camera
{
	public float x = 0;
	public float y = 0;
	public final int width;
	public final int height;
	
	public Camera(int width, int height)
	{
		this.width = width;
		this.height = height;
	}
	
	public boolean isInside(Sprite sprite)
	{
		float right = this.x + this.width;
		float top = this.y + this.height;
		
		return (!((right < sprite.x) || (top < sprite.y) || ((sprite.x + sprite.width) < this.x) || ((sprite.y + sprite.height) < this.y)));
	}
}