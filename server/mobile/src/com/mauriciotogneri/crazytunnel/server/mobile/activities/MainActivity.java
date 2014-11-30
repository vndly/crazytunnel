package com.mauriciotogneri.crazytunnel.server.mobile.activities;

import java.net.InetAddress;
import java.net.SocketException;
import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import com.mauriciotogneri.crazytunnel.server.connection.Game;
import com.mauriciotogneri.crazytunnel.server.connection.Game.GameEvent;
import com.mauriciotogneri.crazytunnel.server.mobile.R;

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
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				startServer();
			}
		});
		thread.start();
	}
	
	private void startServer()
	{
		try
		{
			Game game = new Game(this, 7777, 2, 3);
			game.start();
		}
		catch (SocketException e)
		{
			e.printStackTrace();
		}
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