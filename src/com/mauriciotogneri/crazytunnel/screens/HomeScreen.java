package com.mauriciotogneri.crazytunnel.screens;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;

public class HomeScreen extends BaseFragment
{
	@Override
	protected void onInitialize()
	{
		NumberPicker numberOfPlayers = findViewById(R.id.number_of_players);
		numberOfPlayers.setMinValue(1);
		numberOfPlayers.setMaxValue(6);
		numberOfPlayers.setValue(4);
		
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
	}
	
	private void createMatch()
	{
		String playerName = getPlayerName();
		int numberOfPlayers = getNumberOfPlayers();
	}
	
	private void joinMatch()
	{
		String playerName = getPlayerName();
	}
	
	private String getPlayerName()
	{
		EditText playerName = findViewById(R.id.player_name);
		
		return playerName.getText().toString();
	}
	
	private int getNumberOfPlayers()
	{
		NumberPicker numberOfPlayers = findViewById(R.id.number_of_players);
		
		return numberOfPlayers.getValue();
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_home;
	}
}