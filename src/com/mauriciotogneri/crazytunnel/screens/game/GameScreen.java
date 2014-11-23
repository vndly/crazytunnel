package com.mauriciotogneri.crazytunnel.screens.game;

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
	private List<Player> players;
	private CustomSurfaceView screen;
	
	public static final String PARAMETER_GAME_CONNECTION = "game_connection";
	public static final String PARAMETER_PLAYERS = "players";
	
	@Override
	protected void onInitialize()
	{
		this.gameConnection = getParameter(GameScreen.PARAMETER_GAME_CONNECTION);
		
		this.players = getParameter(GameScreen.PARAMETER_PLAYERS);
		
		this.game = new Game(this);
		this.gameConnection.setListener(this.game);
		
		this.screen = findViewById(R.id.glSurface);
		this.screen.setRenderer(new Renderer(this.game, getContext(), this.screen));
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