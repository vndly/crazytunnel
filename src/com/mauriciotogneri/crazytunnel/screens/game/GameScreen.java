package com.mauriciotogneri.crazytunnel.screens.game;

import java.util.ArrayList;
import java.util.List;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.engine.CustomSurfaceView;
import com.mauriciotogneri.crazytunnel.engine.Game;
import com.mauriciotogneri.crazytunnel.engine.Renderer;
import com.mauriciotogneri.crazytunnel.objects.Player;

public class GameScreen extends BaseFragment
{
	private Game game;
	private GameConnection gameConnection;
	private CustomSurfaceView screen;
	
	public static final String PARAMETER_GAME_CONNECTION = "game_connection";
	public static final String PARAMETER_PLAYER = "player";
	public static final String PARAMETER_PLAYERS = "players";
	
	@Override
	protected void onInitialize()
	{
		this.gameConnection = getParameter(GameScreen.PARAMETER_GAME_CONNECTION);
		
		Player player = getParameter(GameScreen.PARAMETER_PLAYER);
		List<Player> players = getParameter(GameScreen.PARAMETER_PLAYERS);
		
		List<Player> enemyPlayers = getEnemyPlayers(player, players);
		
		this.game = new Game(this, this.gameConnection, player, enemyPlayers, this.gameConnection.isServer());
		this.gameConnection.setListener(this.game);
		
		this.screen = findViewById(R.id.glSurface);
		this.screen.setRenderer(new Renderer(this.game, getContext(), this.screen));
		
		showToast("PLAYING AS : " + player.id);
	}
	
	private List<Player> getEnemyPlayers(Player player, List<Player> players)
	{
		List<Player> result = new ArrayList<Player>();
		
		for (Player currentPlayer : players)
		{
			if (currentPlayer.id != player.id)
			{
				result.add(currentPlayer);
			}
		}
		
		return result;
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_game;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (this.game != null)
		{
			this.game.resume();
		}
		
		if (this.screen != null)
		{
			this.screen.onResume();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if (this.game != null)
		{
			this.game.pause(getBaseActivity().isFinishing());
		}
		
		if (this.screen != null)
		{
			this.screen.onPause();
		}
	}
	
	@Override
	public void onDestroy()
	{
		if (this.game != null)
		{
			this.game.stop();
		}
		
		super.onDestroy();
	}
}