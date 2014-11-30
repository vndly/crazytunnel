package com.mauriciotogneri.crazytunnel.client.screens.home;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.client.screens.lobby.LobbyScreen;
import com.mauriciotogneri.crazytunnel.client.shapes.Preferences;

public class HomeScreen extends BaseFragment
{
	@Override
	protected void onInitialize()
	{
		Button joinGame = findViewById(R.id.join_game);
		joinGame.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				joinGame();
			}
		});
		
		EditText playerName = findViewById(R.id.player_name);
		playerName.setText(Preferences.getPlayerName());
		
		EditText serverIP = findViewById(R.id.server_ip);
		serverIP.setText(Preferences.getServerIP());
		
		EditText serverPort = findViewById(R.id.server_port);
		serverPort.setText(String.valueOf(Preferences.getServerPort()));
	}
	
	private void joinGame()
	{
		String playerName = getPlayerName();
		String serverIP = getServerIP();
		int serverPort = getServerPort();
		
		if (playerName.isEmpty())
		{
			showToast("MISSING NAME");
		}
		else if (serverIP.isEmpty())
		{
			showToast("MISSING SERVER IP");
		}
		else if (serverPort == 0)
		{
			showToast("MISSING SERVER PORT");
		}
		else
		{
			Preferences.setPlayerName(playerName);
			Preferences.setServerIP(serverIP);
			Preferences.setServerPort(serverPort);
			
			LobbyScreen lobbyScreen = new LobbyScreen();
			lobbyScreen.setParameter(LobbyScreen.PARAMETER_PLAYER_NAME, playerName);
			lobbyScreen.setParameter(LobbyScreen.PARAMETER_SERVER_IP, serverIP);
			lobbyScreen.setParameter(LobbyScreen.PARAMETER_SERVER_PORT, serverPort);
			openFragment(lobbyScreen);
		}
	}
	
	private String getPlayerName()
	{
		EditText playerName = findViewById(R.id.player_name);
		
		return playerName.getText().toString();
	}
	
	private String getServerIP()
	{
		EditText serverIP = findViewById(R.id.server_ip);
		
		return serverIP.getText().toString();
	}
	
	private int getServerPort()
	{
		EditText serverIP = findViewById(R.id.server_port);
		
		return Integer.parseInt(serverIP.getText().toString());
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_home;
	}
}