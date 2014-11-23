package com.mauriciotogneri.crazytunnel.shapes;

public class Rectangle extends Shape
{
	public Rectangle(float width, float height, int color)
	{
		super(color, new float[]
			{
			    0, height, //
			    0, 0, //
			    width, height, //
			    width, 0
			});
	}
}