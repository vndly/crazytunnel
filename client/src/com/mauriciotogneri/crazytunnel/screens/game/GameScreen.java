package com.mauriciotogneri.crazytunnel.screens.game;

import java.util.List;
import com.mauriciotogneri.crazytunnel.Player;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.connection.tcp.ClientConnection;
import com.mauriciotogneri.crazytunnel.engine.CustomSurfaceView;
import com.mauriciotogneri.crazytunnel.engine.Game;
import com.mauriciotogneri.crazytunnel.engine.Renderer;

public class GameScreen extends BaseFragment
{
	private Game game;
	private CustomSurfaceView screen;
	
	public static final String PARAMETER_PLAYER = "player";
	public static final String PARAMETER_ENEMIES = "enemies";
	public static final String PARAMETER_CONNECTION = "coonection";
	public static final String PARAMETER_LAPS = "laps";
	
	@Override
	protected void onInitialize()
	{
		Player player = getParameter(GameScreen.PARAMETER_PLAYER);
		List<Player> enemies = getParameter(GameScreen.PARAMETER_ENEMIES);
		
		ClientConnection clientConnection = getParameter(GameScreen.PARAMETER_CONNECTION);
		
		int laps = getParameter(GameScreen.PARAMETER_LAPS);
		
		this.game = new Game(this, clientConnection, player, enemies, laps);
		
		this.screen = findViewById(R.id.glSurface);
		this.screen.setRenderer(new Renderer(this.game, getContext(), this.screen));
	}
	
	public void onDisconnect()
	{
		finish();
		showToast("DISCONNECTED");
	}
	
	public void showMessage(String message)
	{
		showToast(message);
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