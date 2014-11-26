package com.mauriciotogneri.crazytunnel.engine;

import java.util.List;
import android.os.Vibrator;
import android.util.SparseArray;
import com.mauriciotogneri.crazytunnel.connection.MessageReader;
import com.mauriciotogneri.crazytunnel.connection.Messages;
import com.mauriciotogneri.crazytunnel.connection.Messages.SetPlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.engine.Alarm.OnAlarmRing;
import com.mauriciotogneri.crazytunnel.input.InputEvent;
import com.mauriciotogneri.crazytunnel.objects.Player;
import com.mauriciotogneri.crazytunnel.objects.box.EnemyBox;
import com.mauriciotogneri.crazytunnel.objects.box.PlayerBox;
import com.mauriciotogneri.crazytunnel.objects.level.Level;
import com.mauriciotogneri.crazytunnel.objects.level.LevelDefinition;
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
	
	private final Player player;
	private final List<Player> enemyPlayers;
	
	private final Camera camera;
	
	private PlayerBox playerBox;
	
	private final SparseArray<EnemyBox> enemyBoxes = new SparseArray<EnemyBox>();
	
	private Level level;
	
	private final Alarm alarmCountdown;
	private final Alarm alarmFinished;
	
	private GameStatus gameStatus = GameStatus.READY;
	
	private enum GameStatus
	{
		READY, // all the players in the starting line
		COUNTDOWN, // the countdown is decreasing
		RUNNING, // the race is on
		FINISHED; // the race is finished
	}
	
	public Game(GameScreen gameScreen, GameConnection gameConnection, Player player, List<Player> enemyPlayers, boolean isServer)
	{
		this.gameScreen = gameScreen;
		this.gameConnection = gameConnection;
		
		this.player = player;
		this.enemyPlayers = enemyPlayers;
		
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
		
		this.alarmFinished = new Alarm(new OnAlarmRing()
		{
			@Override
			public boolean onAlarmRing()
			{
				leaderboardFinished();
				
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
			this.playerBox = new PlayerBox(this.camera, this.level, vibrator, 0, Renderer.RESOLUTION_Y / 2, this.player.color);
			
			for (Player player : this.enemyPlayers)
			{
				EnemyBox box = new EnemyBox(this.camera, this.level, 0, Renderer.RESOLUTION_Y / 2, player.color);
				this.enemyBoxes.put(player.id, box);
			}
		}
	}
	
	private void restart()
	{
		this.playerBox.restart();
		
		for (int i = 0, size = this.enemyBoxes.size(); i < size; i++)
		{
			EnemyBox box = this.enemyBoxes.valueAt(i);
			box.restart();
		}
	}
	
	private LevelDefinition getLevelDefinition()
	{
		LevelDefinition result = new LevelDefinition(200, 3);
		
		Shape obstacle = new Rectangle(5, 15, LevelDefinition.WALL_COLOR);
		
		result.add(new Sprite(obstacle, 80, 5));
		result.add(new Sprite(obstacle, 130, Renderer.RESOLUTION_Y - 20));
		
		result.build();
		
		return result;
	}
	
	// ======================== UPDATE ====================== \\
	
	private boolean lastInput = false;
	
	public void update(float delta, InputEvent input, Renderer renderer)
	{
		switch (this.gameStatus)
		{
			case RUNNING:
				processRunning(delta, input);
				break;
			
			case READY:
				processReady();
				break;
			
			case COUNTDOWN:
				this.alarmCountdown.step(delta);
				break;
			
			case FINISHED:
				this.alarmFinished.step(delta);
				break;
		}
		
		render(renderer, this.camera, this.level, this.playerBox, this.enemyBoxes);
	}
	
	private void processReady()
	{
		restart();
		
		if (this.isServer)
		{
			this.gameStatus = GameStatus.COUNTDOWN;
			this.alarmCountdown.restart();
		}
	}
	
	private void processRunning(float delta, InputEvent input)
	{
		this.playerBox.update(delta, input);
		broadcastBoxPosition(this.player, this.playerBox, input);
		
		for (int i = 0, size = this.enemyBoxes.size(); i < size; i++)
		{
			EnemyBox box = this.enemyBoxes.valueAt(i);
			box.update(delta);
		}
		
		if (this.isServer && this.playerBox.finished() && enemiesFinished(this.enemyBoxes))
		{
			this.gameStatus = GameStatus.FINISHED;
			
			this.alarmFinished.restart();
		}
	}
	
	private boolean enemiesFinished(SparseArray<EnemyBox> enemyBoxes)
	{
		boolean result = true;
		
		for (int i = 0, size = enemyBoxes.size(); i < size; i++)
		{
			EnemyBox box = enemyBoxes.valueAt(i);
			
			if (!box.finished())
			{
				result = false;
				break;
			}
		}
		
		return result;
	}
	
	private void render(Renderer renderer, Camera camera, Level level, PlayerBox playerBox, SparseArray<EnemyBox> enemyBoxes)
	{
		focusCamera(camera, playerBox);
		renderer.clearScreen(camera);
		
		level.render(renderer);
		
		for (int i = 0, size = enemyBoxes.size(); i < size; i++)
		{
			EnemyBox box = enemyBoxes.valueAt(i);
			box.render(renderer);
		}
		
		playerBox.render(renderer);
	}
	
	private void broadcastBoxPosition(Player player, PlayerBox box, InputEvent input)
	{
		if (this.lastInput != input.jump)
		{
			this.lastInput = input.jump;
			
			this.gameConnection.send(Messages.SetPlayerBoxPosition.create(player, box, input.jump), true);
		}
	}
	
	private void focusCamera(Camera camera, PlayerBox playerBox)
	{
		camera.x = playerBox.getX() - 40;
	}
	
	private void countdownFinished()
	{
		this.gameConnection.send(Messages.StartRace.create(), true);
		
		startRace();
	}
	
	private void leaderboardFinished()
	{
		this.gameConnection.send(Messages.RestartRace.create(), true);
		
		restartRace();
	}
	
	private void restartRace()
	{
		this.gameStatus = GameStatus.READY;
	}
	
	private void startRace()
	{
		this.gameStatus = GameStatus.RUNNING;
	}
	
	private void updateBoxPosition(SetPlayerBoxPosition setPlayerBoxPosition)
	{
		if (this.isServer)
		{
			Player player = getPlayerById(setPlayerBoxPosition.playerId);
			
			if (player != null)
			{
				this.gameConnection.send(player.macAddress, setPlayerBoxPosition.create(), false);
			}
		}
		
		EnemyBox box = this.enemyBoxes.get(setPlayerBoxPosition.playerId);
		
		if (box != null)
		{
			box.update(setPlayerBoxPosition.x, setPlayerBoxPosition.y, setPlayerBoxPosition.jumping);
		}
	}
	
	private Player getPlayerById(int id)
	{
		Player result = null;
		
		for (Player player : this.enemyPlayers)
		{
			if (player.id == id)
			{
				result = player;
				break;
			}
		}
		
		return result;
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
				
				case Messages.RestartRace.CODE:
					restartRace();
					break;
				
				case Messages.SetPlayerBoxPosition.CODE:
					updateBoxPosition(new SetPlayerBoxPosition(reader));
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
		this.gameScreen.disconnected();
	}
}