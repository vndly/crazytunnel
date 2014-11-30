package com.mauriciotogneri.crazytunnel.client.screens.game;

import java.util.List;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection;
import com.mauriciotogneri.crazytunnel.client.engine.CustomSurfaceView;
import com.mauriciotogneri.crazytunnel.client.engine.Game;
import com.mauriciotogneri.crazytunnel.client.engine.Renderer;
import com.mauriciotogneri.crazytunnel.client.screens.ranking.RankingScreen;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.objects.Player;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;

public class GameScreen extends BaseFragment
{
	private Game game;
	private CustomSurfaceView screen;
	private RankingScreen rankingScreen;
	
	public static final String PARAMETER_PLAYER = "player";
	public static final String PARAMETER_ENEMIES = "enemies";
	public static final String PARAMETER_CONNECTION_TCP = "coonection_tcp";
	public static final String PARAMETER_CONNECTION_UDP = "coonection_udp";
	public static final String PARAMETER_SERVER_UDP_PORT = "server_udp_port";
	public static final String PARAMETER_LAPS = "laps";
	
	@Override
	protected void onInitialize()
	{
		Player player = getParameter(GameScreen.PARAMETER_PLAYER);
		List<Player> enemies = getParameter(GameScreen.PARAMETER_ENEMIES);
		
		ClientConnection clientConnection = getParameter(GameScreen.PARAMETER_CONNECTION_TCP);
		DatagramCommunication datagramCommunication = getParameter(GameScreen.PARAMETER_CONNECTION_UDP);
		int udpPort = getParameter(GameScreen.PARAMETER_SERVER_UDP_PORT);
		
		int laps = getParameter(GameScreen.PARAMETER_LAPS);
		
		this.game = new Game(this, clientConnection, datagramCommunication, udpPort, player, enemies, laps);
		
		this.screen = findViewById(R.id.glSurface);
		this.screen.setRenderer(new Renderer(this.game, getContext(), this.screen));
		
		this.rankingScreen = new RankingScreen();
		
		this.rankingScreen.setParameter(RankingScreen.PARAMETER_GAME_SCREEN, this);
		openFragment(this.rankingScreen);
	}
	
	public void displayRanking()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				GameScreen.this.rankingScreen.setVisibility(true);
			}
		});
	}
	
	public void updateRankingList(final RankingRow[] ranking, final boolean enableReady)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				GameScreen.this.rankingScreen.updateRankingList(ranking, enableReady);
			}
		});
	}
	
	public void playerReady()
	{
		GameScreen.this.rankingScreen.setVisibility(false);
		
		this.game.restartRace();
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