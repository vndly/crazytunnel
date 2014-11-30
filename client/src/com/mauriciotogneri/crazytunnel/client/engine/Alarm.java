package com.mauriciotogneri.crazytunnel.client.engine;

public class Alarm
{
	private final OnAlarmRing callback;
	private final long duration; // in milliseconds
	private float timeElapsed = 0;
	private boolean activated = false;
	
	public Alarm(OnAlarmRing listener, long duration)
	{
		this.callback = listener;
		this.duration = duration;
	}
	
	public void step(float delta)
	{
		if (this.activated)
		{
			this.timeElapsed += (delta * 1E3f);
			
			if (this.timeElapsed >= this.duration)
			{
				this.activated = this.callback.onAlarmRing();
				this.timeElapsed -= this.duration;
			}
		}
	}
	
	public void restart()
	{
		this.timeElapsed = 0;
		this.activated = true;
	}
	
	public boolean isActivated()
	{
		return this.activated;
	}
	
	public interface OnAlarmRing
	{
		boolean onAlarmRing();
	}
}