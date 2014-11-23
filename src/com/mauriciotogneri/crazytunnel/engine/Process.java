package com.mauriciotogneri.crazytunnel.engine;

import android.util.SparseArray;
import com.mauriciotogneri.crazytunnel.engine.Alarm.OnAlarmRing;

public class Process
{
	private int nextAlarmId = 1;
	private final SparseArray<Alarm> alarms = new SparseArray<Alarm>();
	
	public final void process(float delta)
	{
		int size = this.alarms.size();
		
		if (size > 0)
		{
			for (int i = 0; i < size; i++)
			{
				Alarm alarm = this.alarms.valueAt(i);
				
				if (alarm.step(delta))
				{
					this.alarms.remove(alarm.getId());
				}
			}
		}
	}
	
	public int setAlarm(OnAlarmRing listener, int milliseconds)
	{
		int id = this.nextAlarmId++;
		
		this.alarms.put(id, new Alarm(id, listener, milliseconds));
		
		return id;
	}
}