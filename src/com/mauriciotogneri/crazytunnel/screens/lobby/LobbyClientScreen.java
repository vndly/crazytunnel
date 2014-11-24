package com.mauriciotogneri.crazytunnel.screens.lobby;

import java.util.ArrayList;
import android.bluetooth.BluetoothDevice;
import android.widget.ListView;
import com.mauriciotogneri.bluetooth.connection.client.ClientConnection;
import com.mauriciotogneri.bluetooth.connection.client.ClientEvent;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.connection.MessageReader;
import com.mauriciotogneri.crazytunnel.connection.Messages;
import com.mauriciotogneri.crazytunnel.connection.Messages.SetFinalPlayersList;
import com.mauriciotogneri.crazytunnel.connection.Messages.SetPlayerInfo;
import com.mauriciotogneri.crazytunnel.connection.Messages.SetRegisteredPlayers;
import com.mauriciotogneri.crazytunnel.objects.Player;
import com.mauriciotogneri.crazytunnel.screens.game.GameConnection;
import com.mauriciotogneri.crazytunnel.screens.game.GameScreen;

public class LobbyClientScreen extends BaseFragment implements ClientEvent
{
	private Player player;
	private ClientConnection clientConnection;
	private PlayerAdapter playerAdapter;
	
	public static final String PARAMETER_SERVER = "server";
	
	@Override
	protected void onInitialize()
	{
		String playerName = getParameter(ServerSelectionScreen.PARAMETER_PLAYER_NAME);
		BluetoothDevice serverDevice = getParameter(LobbyClientScreen.PARAMETER_SERVER);
		
		this.playerAdapter = new PlayerAdapter(getContext(), new ArrayList<Player>());
		
		ListView listView = (ListView)findViewById(R.id.list_of_players);
		listView.setAdapter(this.playerAdapter);
		
		this.clientConnection = new ClientConnection(this);
		this.clientConnection.connect(serverDevice, LobbyServerScreen.UUID);
		
		this.player = new Player();
		this.player.name = playerName;
	}
	
	private void send(byte[] message)
	{
		this.clientConnection.send(message, true);
	}
	
	@Override
	protected void onClose()
	{
		this.clientConnection.close();
	}
	
	@Override
	public void onReceive(byte[] message)
	{
		if (message.length > 0)
		{
			MessageReader reader = new MessageReader(message);
			byte code = reader.getByte();
			
			switch (code)
			{
				case Messages.SetPlayerInfo.CODE:
					processSetPlayerInfo(new SetPlayerInfo(reader));
					break;
				
				case Messages.SetRegisteredPlayers.CODE:
					processSetRegisteredPlayers(new SetRegisteredPlayers(reader));
					break;
				
				case Messages.SetFinalPlayersList.CODE:
					processSetFinalPlayersList(new SetFinalPlayersList(reader));
					break;
			}
		}
	}
	
	private void processSetPlayerInfo(SetPlayerInfo setPlayerInfo)
	{
		this.player.id = setPlayerInfo.id;
		this.player.color = setPlayerInfo.color;
		
		send(Messages.SetPlayerName.create(this.player.name, this.player.color));
	}
	
	private void processSetRegisteredPlayers(final SetRegisteredPlayers setRegisteredPlayers)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				refreshPlayerList(setRegisteredPlayers.players);
			}
		});
	}
	
	private void processSetFinalPlayersList(SetFinalPlayersList setFinalPlayersList)
	{
		GameConnection gameConnection = new GameConnection(this.clientConnection);
		
		GameScreen gameScreen = new GameScreen();
		gameScreen.setParameter(GameScreen.PARAMETER_GAME_CONNECTION, gameConnection);
		gameScreen.setParameter(GameScreen.PARAMETER_PLAYER, this.player);
		gameScreen.setParameter(GameScreen.PARAMETER_PLAYERS, setFinalPlayersList.players);
		openFragment(gameScreen);
	}
	
	private void refreshPlayerList(Player[] players)
	{
		this.playerAdapter.clear();
		
		for (Player player : players)
		{
			this.playerAdapter.add(player);
		}
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_lobby_client;
	}
	
	@Override
	public void onConnect()
	{
	}
	
	@Override
	public void onErrorConnecting()
	{
		showToast("Error connecting...");
		this.clientConnection.close();
		close();
	}
	
	@Override
	public void onDisconnect()
	{
		showToast("Disconnected...");
		this.clientConnection.close();
		close();
	}
}