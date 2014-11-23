package com.mauriciotogneri.crazytunnel.engine;

import android.graphics.Color;
import android.os.Vibrator;
import com.mauriciotogneri.crazytunnel.connection.MessageReader;
import com.mauriciotogneri.crazytunnel.connection.Messages;
import com.mauriciotogneri.crazytunnel.engine.Alarm.OnAlarmRing;
import com.mauriciotogneri.crazytunnel.input.InputEvent;
import com.mauriciotogneri.crazytunnel.objects.Level;
import com.mauriciotogneri.crazytunnel.objects.LevelDefinition;
import com.mauriciotogneri.crazytunnel.objects.PlayerBox;
import com.mauriciotogneri.crazytunnel.screens.game.GameConnection;
import com.mauriciotogneri.crazytunnel.screens.game.GameEvent;
import com.mauriciotogneri.crazytunnel.screens.game.GameScreen;
import com.mauriciotogneri.crazytunnel.shapes.Rectangle;
import com.mauriciotogneri.crazytunnel.shapes.Shape;

public class Game implements GameEvent
{
	private final GameScreen gameScreen;
	private final GameConnection gameConnection;
	private final boolean isServer;
	private Renderer renderer;
	
	private final Camera camera;
	
	private PlayerBox playerBox;
	private Level level;
	
	private final Alarm alarmCountdown;
	
	private GameStatus gameStatus = GameStatus.READY;
	
	private enum GameStatus
	{
		READY, // all the players in the starting line
		COUNTDOWN, // the countdown is decreasing
		RUNNING; // the race is on
	}
	
	public Game(GameScreen gameScreen, GameConnection gameConnection, boolean isServer)
	{
		this.gameScreen = gameScreen;
		this.gameConnection = gameConnection;
		this.isServer = isServer;
		this.camera = new Camera(Renderer.RESOLUTION_X, Renderer.RESOLUTION_Y);
		
		this.alarmCountdown = new Alarm(new OnAlarmRing()
		{
			@Override
			public boolean onAlarmRing()
			{
				countdownFinished();
				
				return false;
			}
		}, 3 * 1000);
	}
	
	public void start(Renderer renderer)
	{
		if (this.renderer == null)
		{
			this.renderer = renderer;
			
			Vibrator vibrator = this.gameScreen.getVibrator();
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
		this.alarmCountdown.step(delta);
		
		switch (this.gameStatus)
		{
			case RUNNING:
				this.playerBox.update(delta, input);
				
				renderer.clearScreen(this.camera);
				this.level.render(renderer);
				this.playerBox.render(renderer);
				break;
			case COUNTDOWN:
				break;
			case READY:
				if (this.isServer)
				{
					this.gameStatus = GameStatus.COUNTDOWN;
					this.alarmCountdown.restart();
				}
				break;
		}
	}
	
	private void countdownFinished()
	{
		this.gameConnection.sendAll(Messages.StartRace.create());
		
		startRace();
	}
	
	private void startRace()
	{
		this.gameStatus = GameStatus.RUNNING;
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
	
	// ========================= CONNECTION ======================
	
	@Override
	public void onReceive(byte[] message)
	{
		if (message.length > 0)
		{
			MessageReader reader = new MessageReader(message);
			byte code = reader.getByte();
			
			switch (code)
			{
				case Messages.StartRace.CODE:
					startRace();
					break;
			}
		}
	}
	
	@Override
	public void playerDisconnect(String macAddress)
	{
		// TODO
	}
	
	@Override
	public void onDisconnect()
	{
		// TODO
	}
}