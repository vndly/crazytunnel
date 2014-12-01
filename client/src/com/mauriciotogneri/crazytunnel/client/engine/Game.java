package com.mauriciotogneri.crazytunnel.client.engine;

import java.net.InetAddress;
import java.util.List;
import android.content.Context;
import android.os.Vibrator;
import android.util.SparseArray;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection.ClientConnectionEvent;
import com.mauriciotogneri.crazytunnel.client.input.InputEvent;
import com.mauriciotogneri.crazytunnel.client.objects.box.EnemyBox;
import com.mauriciotogneri.crazytunnel.client.objects.box.PlayerBox;
import com.mauriciotogneri.crazytunnel.client.objects.level.Level;
import com.mauriciotogneri.crazytunnel.client.objects.level.LevelDefinition;
import com.mauriciotogneri.crazytunnel.client.screens.game.GameScreen;
import com.mauriciotogneri.crazytunnel.client.util.ConnectionUtils;
import com.mauriciotogneri.crazytunnel.common.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.common.messages.Messages;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerBoxPosition;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.RankingList;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication.DatagramCommunicationEvent;
import com.mauriciotogneri.crazytunnel.common.objects.Player;

public class Game implements ClientConnectionEvent, DatagramCommunicationEvent
{
	private final GameScreen gameScreen;
	private Renderer renderer;
	
	private final InetAddress udpAddress;
	private final int udpPort;
	private final DatagramCommunication connection;
	private final ClientConnection clientConnection;
	
	private final Player player;
	private final List<Player> enemies;
	
	private final Camera camera;
	
	private double startTime = 0;
	private final PlayerBox playerBox;
	private final SparseArray<EnemyBox> enemyBoxes = new SparseArray<EnemyBox>();
	
	private final Level level;
	
	private GameStatus gameStatus = GameStatus.READY;
	
	private enum GameStatus
	{
		READY, // all the players in the starting line
		RUNNING, // the race is on
		FINISHED; // the race is finished
	}
	
	public Game(GameScreen gameScreen, ClientConnection clientConnection, DatagramCommunication connection, int udpPort, Player player, List<Player> enemies, int laps)
	{
		this.gameScreen = gameScreen;
		
		this.udpPort = udpPort;
		this.connection = connection;
		this.connection.setCallback(this);
		
		this.clientConnection = clientConnection;
		this.clientConnection.setCallback(this);
		
		this.udpAddress = this.clientConnection.getRemoteAddress();
		
		this.player = player;
		this.enemies = enemies;
		
		this.camera = new Camera(Renderer.RESOLUTION_X, Renderer.RESOLUTION_Y);
		
		Vibrator vibrator = gameScreen.getVibrator();
		LevelDefinition levelDefinition = getLevelDefinition(gameScreen.getContext(), R.raw.map4, laps);
		
		this.level = new Level(this.camera, levelDefinition);
		
		this.playerBox = new PlayerBox(this.camera, this.level, vibrator, 0, Renderer.RESOLUTION_Y / 2, this.player.color);
		
		for (Player enemyPlayer : this.enemies)
		{
			EnemyBox box = new EnemyBox(this.camera, this.level, 0, Renderer.RESOLUTION_Y / 2, enemyPlayer.color);
			this.enemyBoxes.put(enemyPlayer.id, box);
		}
	}
	
	public void setRenderer(Renderer renderer)
	{
		if (this.renderer == null)
		{
			this.renderer = renderer;
			
			restartRace();
		}
	}
	
	private LevelDefinition getLevelDefinition(Context context, int mapId, int laps)
	{
		LevelDefinition result = new LevelDefinition(context, mapId, laps);
		result.build();
		
		return result;
	}
	
	// ======================== UPDATE ====================== \\
	
	public void update(double delta, InputEvent input, Renderer renderer)
	{
		switch (this.gameStatus)
		{
			case RUNNING:
				processRunning(delta, input);
				break;
			
			case READY:
				break;
			
			case FINISHED:
				break;
		}
		
		render(renderer, this.camera, this.level, this.playerBox, this.enemyBoxes);
	}
	
	private void processRunning(double delta, InputEvent input)
	{
		this.playerBox.update(delta, input);
		
		broadcastBoxPosition(this.player, this.playerBox, input);
		
		for (int i = 0, size = this.enemyBoxes.size(); i < size; i++)
		{
			this.enemyBoxes.valueAt(i).update(delta);
		}
		
		if (this.playerBox.finished())
		{
			this.playerBox.pause();
			
			this.gameStatus = GameStatus.FINISHED;
			
			this.gameScreen.displayRanking();
			
			double totalTime = (System.nanoTime() - this.startTime) / 1E9d;
			ConnectionUtils.send(this.clientConnection, Messages.PlayerFinished.create(this.player.name, this.player.color, totalTime));
		}
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
	
	private void focusCamera(Camera camera, PlayerBox playerBox)
	{
		camera.x = playerBox.getX() - 40;
	}
	
	public void restartRace()
	{
		this.gameStatus = GameStatus.READY;
		
		this.playerBox.restart();
		
		for (int i = 0, size = this.enemyBoxes.size(); i < size; i++)
		{
			EnemyBox box = this.enemyBoxes.valueAt(i);
			box.restart();
		}
		
		ConnectionUtils.send(this.clientConnection, Messages.Ready.create());
	}
	
	private void startRace()
	{
		this.gameStatus = GameStatus.RUNNING;
		
		this.startTime = System.nanoTime();
	}
	
	private void broadcastBoxPosition(Player player, PlayerBox box, InputEvent input)
	{
		// if (this.lastInput != input.jump)
		// {
		// this.lastInput = input.jump;
		
		ConnectionUtils.send(this.connection, this.udpAddress, this.udpPort, Messages.PlayerBoxPosition.create(player.id, box.getX(), box.getY(), input.jump));
		
		// ConnectionUtils.send(this.clientConnection, message);
		// }
	}
	
	// private long lastTime = (System.nanoTime() / 1000000);
	
	private void updateBoxPosition(PlayerBoxPosition playerBoxPosition)
	{
		// long now = (System.nanoTime() / 1000000);
		// System.out.println("TEST " + (now - this.lastTime) + " = " + playerBoxPosition.x);
		// this.lastTime = now;
		
		EnemyBox box = this.enemyBoxes.get(playerBoxPosition.playerId);
		
		if (box != null)
		{
			box.update(playerBoxPosition.x, playerBoxPosition.y, playerBoxPosition.jumping);
		}
	}
	
	private void processRankingList(RankingList rankingList)
	{
		this.gameScreen.updateRankingList(rankingList.ranking, rankingList.ranking.length == (this.enemies.size() + 1));
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
	public void onConnect()
	{
	}
	
	@Override
	public void onErrorConnecting()
	{
	}
	
	@Override
	public void onDisconnect()
	{
		this.gameScreen.onDisconnect();
	}
	
	@Override
	public void onReceive(byte[] message)
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
			
			case Messages.RankingList.CODE:
				processRankingList(new RankingList(reader));
				break;
		}
	}
	
	@Override
	public void onReceive(InetAddress address, int port, byte[] message)
	{
		MessageReader reader = new MessageReader(message);
		byte code = reader.getByte();
		
		switch (code)
		{
			case Messages.PlayerBoxPosition.CODE:
				updateBoxPosition(new PlayerBoxPosition(reader));
				break;
		}
	}
}