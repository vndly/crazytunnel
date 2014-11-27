package com.mauriciotogneri.crazytunnel.screens.home;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.screens.lobby.LobbyServerScreen;
import com.mauriciotogneri.crazytunnel.screens.lobby.ServerSelectionScreen;
import com.mauriciotogneri.crazytunnel.shapes.Preferences;

public class HomeScreen extends BaseFragment
{
	@Override
	protected void onInitialize()
	{
		NumberPicker numberOfPlayers = findViewById(R.id.number_of_players);
		numberOfPlayers.setMinValue(1);
		numberOfPlayers.setMaxValue(6);
		numberOfPlayers.setValue(4);
		
		NumberPicker numberOfLaps = findViewById(R.id.number_of_laps);
		numberOfLaps.setMinValue(1);
		numberOfLaps.setMaxValue(10);
		numberOfLaps.setValue(5);
		
		Button createMatch = findViewById(R.id.create_game);
		createMatch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				createMatch();
			}
		});
		
		Button joinMatch = findViewById(R.id.join_game);
		joinMatch.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				joinMatch();
			}
		});
		
		EditText playerName = findViewById(R.id.player_name);
		playerName.setText(Preferences.getPlayerName());
	}
	
	private void createMatch()
	{
		String playerName = getPlayerName();
		
		if (playerName.isEmpty())
		{
			showToast("MISSING NAME");
		}
		else
		{
			Preferences.setPlayerName(playerName);
			
			int numberOfPlayers = getNumberOfPlayers();
			int numberOfLaps = getNumberOfLaps();
			
			LobbyServerScreen lobbyServer = new LobbyServerScreen();
			lobbyServer.setParameter(LobbyServerScreen.PARAMETER_PLAYER_NAME, playerName);
			lobbyServer.setParameter(LobbyServerScreen.PARAMETER_NUMBER_OF_PLAYERS, numberOfPlayers);
			lobbyServer.setParameter(LobbyServerScreen.PARAMETER_NUMBER_OF_LAPS, numberOfLaps);
			openFragment(lobbyServer);
		}
	}
	
	private void joinMatch()
	{
		String playerName = getPlayerName();
		
		if (playerName.isEmpty())
		{
			showToast("MISSING NAME");
		}
		else
		{
			Preferences.setPlayerName(playerName);
			
			ServerSelectionScreen serverSelection = new ServerSelectionScreen();
			serverSelection.setParameter(ServerSelectionScreen.PARAMETER_PLAYER_NAME, playerName);
			openFragment(serverSelection);
			
		}
	}
	
	private String getPlayerName()
	{
		EditText playerName = findViewById(R.id.player_name);
		String result = playerName.getText().toString();
		
		return result;
	}
	
	private int getNumberOfPlayers()
	{
		NumberPicker numberOfPlayers = findViewById(R.id.number_of_players);
		
		return numberOfPlayers.getValue();
	}
	
	private int getNumberOfLaps()
	{
		NumberPicker numberOfLaps = findViewById(R.id.number_of_laps);
		
		return numberOfLaps.getValue();
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_home;
	}
}