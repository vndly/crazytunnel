package com.mauriciotogneri.crazytunnel.screens.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.mauriciotogneri.bluetooth.connection.server.ServerConnection;
import com.mauriciotogneri.bluetooth.connection.server.ServerEvent;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.connection.MessageReader;
import com.mauriciotogneri.crazytunnel.connection.Messages;
import com.mauriciotogneri.crazytunnel.connection.Messages.SetPlayerName;
import com.mauriciotogneri.crazytunnel.objects.Player;

public class LobbyServerScreen extends BaseFragment implements ServerEvent
{
	private ServerConnection serverConnection;
	private PlayerAdapter playerAdapter;
	
	private static final int VISIBILITY_DURATION = 60;
	public static final String UUID = "a6989332-69a6-11e4-b116-123b93f75cba";
	
	private final Map<String, Player> registeredPlayers = new HashMap<String, Player>();
	
	private final Object colorLock = new Object();
	private final SparseBooleanArray colorIndex = new SparseBooleanArray();
	
	@Override
	protected void onInitialize()
	{
		this.colorIndex.put(Color.BLUE, false);
		this.colorIndex.put(Color.RED, false);
		this.colorIndex.put(Color.GREEN, false);
		this.colorIndex.put(Color.YELLOW, false);
		this.colorIndex.put(Color.CYAN, false);
		this.colorIndex.put(Color.MAGENTA, false);
		
		Button startGame = findViewById(R.id.start_game);
		startGame.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				startGame();
			}
		});
		
		this.playerAdapter = new PlayerAdapter(getContext(), new ArrayList<Player>());
		
		ListView listView = (ListView)findViewById(R.id.list_of_players);
		listView.setAdapter(this.playerAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				Player player = (Player)parent.getItemAtPosition(position);
				// TODO
			}
		});
		
		this.serverConnection = new ServerConnection(this, getContext());
		this.serverConnection.listen(LobbyServerScreen.UUID, 2, LobbyServerScreen.VISIBILITY_DURATION);
		
		TextView deviceAddress = (TextView)findViewById(R.id.device_address);
		deviceAddress.setText(this.serverConnection.getDeviceName() + "\r\n" + this.serverConnection.getDeviceAddress());
	}
	
	private void send(Player player, byte[] message)
	{
		this.serverConnection.send(player.device, message);
	}
	
	private void disconnect()
	{
		this.serverConnection.close();
		finish();
	}
	
	@Override
	protected void onClose()
	{
		disconnect();
	}
	
	@Override
	public void onReceive(BluetoothDevice device, byte[] message)
	{
		if (message.length > 0)
		{
			MessageReader reader = new MessageReader(message);
			byte code = reader.getByte();
			
			switch (code)
			{
				case Messages.SetFreeColor.CODE:
					// TODO: ONLY IN CLIENT
					break;
				
				case Messages.SetPlayerName.CODE:
					processSetPlayerName(device, new SetPlayerName(reader));
					break;
			}
		}
	}
	
	private void processSetPlayerName(BluetoothDevice device, SetPlayerName setPlayerName)
	{
		if (!this.registeredPlayers.containsKey(device.getAddress()))
		{
			final Player player = new Player(device);
			
			if (acquireColor(setPlayerName.color))
			{
				player.name = setPlayerName.name;
				player.color = setPlayerName.color;
				
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						addPlayerToList(player);
					}
				});
			}
			
			// SEND CONFIRMATION
			// send(player, Messages.SetFreeColor.create(freeColor));
		}
	}
	
	private int getFreeColor()
	{
		int result = 0;
		
		synchronized (this.colorLock)
		{
			for (int i = 0; i < this.colorIndex.size(); i++)
			{
				int color = this.colorIndex.keyAt(i);
				
				if (!this.colorIndex.get(color))
				{
					result = color;
					break;
				}
			}
		}
		
		return result;
	}
	
	private void freeColor(int color)
	{
		synchronized (this.colorLock)
		{
			this.colorIndex.put(color, false);
		}
	}
	
	private boolean acquireColor(int color)
	{
		boolean result = false;
		
		synchronized (this.colorLock)
		{
			if (!this.colorIndex.get(color))
			{
				result = true;
				this.colorIndex.put(color, true);
			}
		}
		
		return result;
	}
	
	private void playerConnected(BluetoothDevice device)
	{
		String macAddress = device.getAddress();
		
		if (!this.registeredPlayers.containsKey(macAddress))
		{
			Player player = new Player(device);
			this.registeredPlayers.put(macAddress, player);
			
			int freeColor = getFreeColor();
			send(player, Messages.SetFreeColor.create(freeColor));
		}
	}
	
	private void playerDisconnected(BluetoothDevice device)
	{
		String macAddress = device.getAddress();
		
		if (this.registeredPlayers.containsKey(macAddress))
		{
			final Player player = this.registeredPlayers.get(macAddress);
			
			if (player.color != 0)
			{
				freeColor(player.color);
			}
			
			this.registeredPlayers.remove(macAddress);
			
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					removePlayerFromList(player);
				}
			});
		}
	}
	
	private void addPlayerToList(Player player)
	{
		this.playerAdapter.add(player);
	}
	
	private void removePlayerFromList(Player player)
	{
		this.playerAdapter.remove(player);
	}
	
	@Override
	public void onConnect(final BluetoothDevice device)
	{
		playerConnected(device);
	}
	
	@Override
	public void onDisconnect(final BluetoothDevice device)
	{
		
		playerDisconnected(device);
	}
	
	@Override
	public void onErrorOpeningConnection()
	{
		showToast("Error opening connection...");
	}
	
	private void startGame()
	{
		// TODO
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_lobby_server;
	}
}