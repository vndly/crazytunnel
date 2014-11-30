package com.mauriciotogneri.crazytunnel.server.mobile.activities;

import java.net.InetAddress;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.mauriciotogneri.crazytunnel.server.core.Game;
import com.mauriciotogneri.crazytunnel.server.core.Game.GameEvent;
import com.mauriciotogneri.crazytunnel.server.mobile.R;
import com.mauriciotogneri.crazytunnel.server.mobile.util.Preferences;

public class MainActivity extends Activity implements GameEvent
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		Preferences.initialize(this);
		
		EditText players = (EditText)findViewById(R.id.players);
		players.setText(String.valueOf(Preferences.getPlayers()));
		
		EditText laps = (EditText)findViewById(R.id.laps);
		laps.setText(String.valueOf(String.valueOf(Preferences.getLaps())));
		
		EditText port = (EditText)findViewById(R.id.port);
		port.setText(String.valueOf(String.valueOf(Preferences.getPort())));
		
		Button start = (Button)findViewById(R.id.start);
		start.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				start();
			}
		});
	}
	
	private void start()
	{
		int players = getPlayers();
		int laps = getLaps();
		int port = getPort();
		
		if (players == 0)
		{
			showToast("MISSING NUMBER OF PLAYERS");
		}
		else if (laps == 0)
		{
			showToast("MISSING NUMBER OF LAPS");
		}
		else if (port == 0)
		{
			showToast("MISSING PORT");
		}
		else
		{
			enableInput(false);
			
			startServer(this, port, players, laps);
		}
	}
	
	private void enableInput(boolean value)
	{
		Button start = (Button)findViewById(R.id.start);
		start.setEnabled(value);
		
		EditText players = (EditText)findViewById(R.id.players);
		players.setEnabled(value);
		
		EditText laps = (EditText)findViewById(R.id.laps);
		laps.setEnabled(value);
		
		EditText port = (EditText)findViewById(R.id.port);
		port.setEnabled(value);
	}
	
	private void showToast(String message)
	{
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	
	private int getPlayers()
	{
		EditText players = (EditText)findViewById(R.id.players);
		
		return Integer.parseInt(players.getText().toString());
	}
	
	private int getLaps()
	{
		EditText laps = (EditText)findViewById(R.id.laps);
		
		return Integer.parseInt(laps.getText().toString());
	}
	
	private int getPort()
	{
		EditText port = (EditText)findViewById(R.id.port);
		
		return Integer.parseInt(port.getText().toString());
	}
	
	private void startServer(final GameEvent gameEvent, final int port, final int players, final int laps)
	{
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					Game game = new Game(gameEvent, port, players, laps);
					game.start();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	private void addLog(final String text)
	{
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				TextView textView = (TextView)findViewById(R.id.log);
				textView.append(text + "\r\n");
			}
		});
	}
	
	@Override
	public void onClientConnected(InetAddress address)
	{
		addLog("NEW CONNECTION: " + address);
	}
	
	@Override
	public void onClientDisconnect(InetAddress address)
	{
		addLog("CLIENT DISCONNECTED: " + address);
	}
	
	@Override
	public void onConnected(InetAddress address, int port)
	{
		addLog("SERVER STARTED: " + address + ":" + port);
	}
	
	@Override
	public void onFinished()
	{
		addLog("SERVER CLOSED");
	}
}