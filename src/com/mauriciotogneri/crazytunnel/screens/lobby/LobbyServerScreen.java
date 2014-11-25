package com.mauriciotogneri.crazytunnel.screens.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
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
import com.mauriciotogneri.crazytunnel.objects.ColorDefinition;
import com.mauriciotogneri.crazytunnel.objects.Player;
import com.mauriciotogneri.crazytunnel.screens.game.GameConnection;
import com.mauriciotogneri.crazytunnel.screens.game.GameScreen;

public class LobbyServerScreen extends BaseFragment implements ServerEvent
{
	private Player player;
	private ServerConnection serverConnection;
	private PlayerAdapter playerAdapter;
	
	private final Object playerIdLock = new Object();
	private byte nextPlayerId = 1;
	
	private static final int VISIBILITY_DURATION = 60;
	public static final String UUID = "e6c3c895-1dcf-4a8d-9e75-9c57c9123cb9";
	
	private final Map<String, Player> registeredPlayers = new HashMap<String, Player>();
	
	private final Object colorLock = new Object();
	private final List<ColorDefinition> colorIndex = new ArrayList<ColorDefinition>();
	
	public static final String PARAMETER_PLAYER_NAME = "player_name";
	public static final String PARAMETER_NUMBER_OF_PLAYERS = "number_of_players";
	
	@Override
	protected void onInitialize()
	{
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 60, 170, 230)));
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 255, 60, 60)));
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 100, 200, 100)));
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 255, 200, 40)));
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 230, 110, 240)));
		this.colorIndex.add(new ColorDefinition(Color.argb(255, 130, 230, 230)));
		
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
				// Player player = (Player)parent.getItemAtPosition(position);
			}
		});
		
		int numberOfPlayers = getParameter(LobbyServerScreen.PARAMETER_NUMBER_OF_PLAYERS);
		
		this.serverConnection = new ServerConnection(this, getContext());
		this.serverConnection.listen(LobbyServerScreen.UUID, numberOfPlayers, LobbyServerScreen.VISIBILITY_DURATION);
		
		TextView deviceAddress = (TextView)findViewById(R.id.device_address);
		deviceAddress.setText(this.serverConnection.getDeviceName() + "\r\n" + this.serverConnection.getDeviceAddress());
		
		int freeColor = getFreeColor();
		
		if (acquireColor(freeColor))
		{
			String playerName = getParameter(LobbyServerScreen.PARAMETER_PLAYER_NAME);
			byte id = getNextPlayerId();
			this.player = new Player(id, this.serverConnection.getDeviceAddress(), playerName, freeColor);
			this.registeredPlayers.put(this.serverConnection.getDeviceAddress(), this.player);
			this.playerAdapter.add(this.player);
		}
	}
	
	private void send(Player player, byte[] message)
	{
		this.serverConnection.send(player.macAddress, message, true);
	}
	
	@Override
	protected void onClose()
	{
		this.serverConnection.close();
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
				case Messages.SetPlayerName.CODE:
					processSetPlayerName(device, new SetPlayerName(reader));
					break;
			}
		}
	}
	
	private void processSetPlayerName(BluetoothDevice device, SetPlayerName setPlayerName)
	{
		String macAddress = device.getAddress();
		
		if (this.registeredPlayers.containsKey(macAddress))
		{
			final Player player = this.registeredPlayers.get(macAddress);
			
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
				
				broadcastListOfPlayers();
			}
		}
	}
	
	private void broadcastListOfPlayers()
	{
		List<Player> list = new ArrayList<Player>();
		
		for (Player player : this.registeredPlayers.values())
		{
			if (player.isValid())
			{
				list.add(player);
			}
		}
		
		this.serverConnection.sendAll(Messages.SetRegisteredPlayers.create(list), true);
	}
	
	private int getFreeColor()
	{
		int result = 0;
		
		synchronized (this.colorLock)
		{
			for (ColorDefinition colorDefinition : this.colorIndex)
			{
				if (!colorDefinition.acquired)
				{
					result = colorDefinition.color;
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
			for (ColorDefinition colorDefinition : this.colorIndex)
			{
				if (colorDefinition.color == color)
				{
					colorDefinition.acquired = false;
					break;
				}
			}
		}
	}
	
	private boolean acquireColor(int color)
	{
		boolean result = false;
		
		synchronized (this.colorLock)
		{
			for (ColorDefinition colorDefinition : this.colorIndex)
			{
				if ((colorDefinition.color == color) && (!colorDefinition.acquired))
				{
					result = true;
					colorDefinition.acquired = true;
					break;
				}
			}
		}
		
		return result;
	}
	
	private void playerConnected(BluetoothDevice device)
	{
		String macAddress = device.getAddress();
		
		if (!this.registeredPlayers.containsKey(macAddress))
		{
			byte id = getNextPlayerId();
			Player player = new Player(id, macAddress);
			this.registeredPlayers.put(macAddress, player);
			
			int freeColor = getFreeColor();
			send(player, Messages.SetPlayerInfo.create(id, freeColor));
		}
	}
	
	private byte getNextPlayerId()
	{
		byte result = 0;
		
		synchronized (this.playerIdLock)
		{
			result = this.nextPlayerId++;
		}
		
		return result;
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
			broadcastListOfPlayers();
			
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
		List<Player> list = new ArrayList<Player>();
		
		for (Player player : this.registeredPlayers.values())
		{
			if (player.isValid())
			{
				list.add(player);
			}
		}
		
		this.serverConnection.sendAll(Messages.SetFinalPlayersList.create(list), true);
		
		GameConnection gameConnection = new GameConnection(this.serverConnection);
		
		GameScreen gameScreen = new GameScreen();
		gameScreen.setParameter(GameScreen.PARAMETER_GAME_CONNECTION, gameConnection);
		gameScreen.setParameter(GameScreen.PARAMETER_PLAYER, this.player);
		gameScreen.setParameter(GameScreen.PARAMETER_PLAYERS, list);
		openFragment(gameScreen);
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_lobby_server;
	}
}