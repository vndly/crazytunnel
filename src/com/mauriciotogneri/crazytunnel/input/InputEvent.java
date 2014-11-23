package com.mauriciotogneri.crazytunnel.input;

public class InputEvent
{
	public boolean jump = false;
	public boolean action = false;
	
	public void press(float x, int resolutionX)
	{
		if (x < (resolutionX / 2))
		{
			this.jump = true;
		}
		else
		{
			this.action = true;
		}
	}
	
	public void release(float x, int resolutionX)
	{
		if (x < (resolutionX / 2))
		{
			this.jump = false;
		}
		else
		{
			this.action = false;
		}
	}
	
	public void clear()
	{
		this.jump = false;
		this.action = false;
	}
}