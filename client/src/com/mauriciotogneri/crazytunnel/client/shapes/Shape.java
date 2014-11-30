package com.mauriciotogneri.crazytunnel.client.shapes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import com.mauriciotogneri.crazytunnel.client.engine.Renderer;

public class Shape
{
	private final int color;
	private final FloatBuffer vertexData;
	private final int length;
	
	private float width = 0;
	private float height = 0;
	
	public Shape(int color, float[] vertices)
	{
		this.color = color;
		
		this.width = getMaxX(vertices);
		this.height = getMaxY(vertices);
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
		byteBuffer.order(ByteOrder.nativeOrder());
		this.vertexData = byteBuffer.asFloatBuffer();
		this.vertexData.put(vertices);
		
		this.length = (vertices.length / 2);
	}
	
	private float getMaxX(float[] vertices)
	{
		float result = Float.MIN_VALUE;
		
		for (int i = 0; i < vertices.length; i += 2)
		{
			float x = vertices[i];
			
			if (x > result)
			{
				result = x;
			}
		}
		
		return result;
	}
	
	private float getMaxY(float[] vertices)
	{
		float result = Float.MIN_VALUE;
		
		for (int i = 1; i < vertices.length; i += 2)
		{
			float y = vertices[i];
			
			if (y > result)
			{
				result = y;
			}
		}
		
		return result;
	}
	
	public float getWidth()
	{
		return this.width;
	}
	
	public float getHeight()
	{
		return this.height;
	}
	
	public void render(Renderer renderer, float x, float y)
	{
		render(renderer, x, y, 1);
	}
	
	public void render(Renderer renderer, float x, float y, float alpha)
	{
		render(renderer, x, y, alpha, 1f, 1f);
	}
	
	public void render(Renderer renderer, float x, float y, float alpha, float scaleX, float scaleY)
	{
		renderer.renderShape(this.vertexData, x, y, this.color, alpha, scaleX, scaleY, this.length);
	}
}