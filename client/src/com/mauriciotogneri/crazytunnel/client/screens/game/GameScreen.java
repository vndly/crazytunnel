package com.mauriciotogneri.crazytunnel.client.screens.game;

import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection;
import com.mauriciotogneri.crazytunnel.client.engine.CustomSurfaceView;
import com.mauriciotogneri.crazytunnel.client.engine.Game;
import com.mauriciotogneri.crazytunnel.client.engine.Renderer;
import com.mauriciotogneri.crazytunnel.client.screens.ranking.RankingAdapter;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.objects.Player;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;

public class GameScreen extends BaseFragment
{
	private Game game;
	private CustomSurfaceView screen;
	private RankingAdapter rankingAdapter;
	
	public static final String PARAMETER_PLAYER = "player";
	public static final String PARAMETER_ENEMIES = "enemies";
	public static final String PARAMETER_CONNECTION_TCP = "coonection_tcp";
	public static final String PARAMETER_CONNECTION_UDP = "coonection_udp";
	public static final String PARAMETER_SERVER_UDP_PORT = "server_udp_port";
	public static final String PARAMETER_LAPS = "laps";
	
	@Override
	protected void onInitialize()
	{
		this.rankingAdapter = new RankingAdapter(getContext(), new ArrayList<RankingRow>());
		
		ListView listView = (ListView)findViewById(R.id.ranking_list);
		listView.setAdapter(this.rankingAdapter);
		
		final Button ready = findViewById(R.id.ready);
		ready.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				playerReady();
				ready.setVisibility(View.INVISIBLE);
			}
		});
		
		Player player = getParameter(GameScreen.PARAMETER_PLAYER);
		List<Player> enemies = getParameter(GameScreen.PARAMETER_ENEMIES);
		
		ClientConnection clientConnection = getParameter(GameScreen.PARAMETER_CONNECTION_TCP);
		DatagramCommunication datagramCommunication = getParameter(GameScreen.PARAMETER_CONNECTION_UDP);
		int udpPort = getParameter(GameScreen.PARAMETER_SERVER_UDP_PORT);
		
		int laps = getParameter(GameScreen.PARAMETER_LAPS);
		
		this.game = new Game(this, clientConnection, datagramCommunication, udpPort, player, enemies, laps);
		
		this.screen = findViewById(R.id.glSurface);
		this.screen.setRenderer(new Renderer(this.game, getContext(), this.screen));
	}
	
	public void displayRanking()
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				displayRankingScreen(true);
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
				update(ranking, enableReady);
			}
		});
	}
	
	private void update(RankingRow[] rankingList, final boolean enableReady)
	{
		this.rankingAdapter.clear();
		
		for (RankingRow ranking : rankingList)
		{
			this.rankingAdapter.add(ranking);
		}
		
		if (enableReady)
		{
			Button ready = findViewById(R.id.ready);
			ready.setVisibility(View.VISIBLE);
		}
	}
	
	public void playerReady()
	{
		displayRankingScreen(false);
		
		this.rankingAdapter.clear();
		
		this.game.restartRace();
	}
	
	private void displayRankingScreen(boolean display)
	{
		LinearLayout rankingScreen = findViewById(R.id.ranking_screen);
		
		if (display)
		{
			rankingScreen.setVisibility(View.VISIBLE);
		}
		else
		{
			rankingScreen.setVisibility(View.INVISIBLE);
		}
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