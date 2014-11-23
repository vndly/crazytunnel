package com.mauriciotogneri.crazytunnel.engine;

import android.content.Context;
import android.graphics.Color;
import android.os.Vibrator;
import com.mauriciotogneri.crazytunnel.activities.MainActivity;
import com.mauriciotogneri.crazytunnel.input.InputEvent;
import com.mauriciotogneri.crazytunnel.objects.Level;
import com.mauriciotogneri.crazytunnel.objects.LevelDefinition;
import com.mauriciotogneri.crazytunnel.objects.PlayerBox;
import com.mauriciotogneri.crazytunnel.shapes.Rectangle;
import com.mauriciotogneri.crazytunnel.shapes.Shape;

public class Game
{
	private final MainActivity mainActivity;
	private Renderer renderer;
	
	private final Camera camera;
	
	private PlayerBox playerBox;
	private Level level;
	
	public Game(MainActivity mainActivity)
	{
		this.camera = new Camera(Renderer.RESOLUTION_X, Renderer.RESOLUTION_Y);
		this.mainActivity = mainActivity;
	}
	
	public void start(Renderer renderer)
	{
		if (this.renderer == null)
		{
			this.renderer = renderer;
			
			Vibrator vibrator = (Vibrator)this.mainActivity.getSystemService(Context.VIBRATOR_SERVICE);
			LevelDefinition levelDefinition = getLevelDefinition();
			
			this.level = new Level(this.camera, levelDefinition);
			this.playerBox = new PlayerBox(this.camera, this.level, vibrator, 0, Renderer.RESOLUTION_Y / 2);
			
			restart();
		}
	}
	
	private LevelDefinition getLevelDefinition()
	{
		LevelDefinition result = new LevelDefinition(200, 3);
		
		int color = Color.argb(255, 90, 110, 120);
		
		Shape wall = new Rectangle(Renderer.RESOLUTION_X * 2, 5, color);
		Sprite wallBottom = new Sprite(wall, 0, 0);
		Sprite wallTop = new Sprite(wall, 0, Renderer.RESOLUTION_Y - 5);
		
		result.add(wallTop);
		result.add(wallBottom);
		
		Shape obstacle = new Rectangle(5, 15, color);
		
		result.add(new Sprite(obstacle, 80, 5));
		result.add(new Sprite(obstacle, 130, Renderer.RESOLUTION_Y - 20));
		
		result.build();
		
		return result;
	}
	
	private void restart()
	{
	}
	
	// ======================== UPDATE ====================== \\
	
	public void update(float delta, InputEvent input, Renderer renderer)
	{
		this.playerBox.update(delta, input);
		
		renderer.clearScreen(this.camera);
		this.level.render(renderer);
		this.playerBox.render(renderer);
	}
	
	// ======================== LIFE CYCLE ====================== \\
	
	public void pause(boolean finishing)
	{
		if (this.renderer != null)
		{
			this.renderer.pause(finishing);
		}
		
		if (this.playerBox != null)
		{
			this.playerBox.pause();
		}
	}
	
	public void resume()
	{
		// TODO: PAUSE AUDIO
	}
	
	public void stop()
	{
		// TODO: STOP AUDIO
	}
}