package com.mauriciotogneri.crazytunnel.client.screens.lobby;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection;
import com.mauriciotogneri.crazytunnel.client.connection.ClientConnection.ClientConnectionEvent;
import com.mauriciotogneri.crazytunnel.client.screens.game.GameScreen;
import com.mauriciotogneri.crazytunnel.client.util.ConnectionUtils;
import com.mauriciotogneri.crazytunnel.common.messages.MessageReader;
import com.mauriciotogneri.crazytunnel.common.messages.Messages;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayerInfo;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.PlayersList;
import com.mauriciotogneri.crazytunnel.common.messages.Messages.StartGame;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication;
import com.mauriciotogneri.crazytunnel.common.network.DatagramCommunication.DatagramCommunicationEvent;
import com.mauriciotogneri.crazytunnel.common.objects.Player;

public class LobbyScreen extends BaseFragment implements ClientConnectionEvent, DatagramCommunicationEvent
{
	private Player player;
	private PlayerAdapter playerAdapter;
	
	private DatagramCommunication datagramCommunication;
	private ClientConnection clientconnection;
	
	private int udpPort = 0;
	private String playerName = "";
	
	public static final String PARAMETER_PLAYER_NAME = "player_name";
	public static final String PARAMETER_SERVER_IP = "server_ip";
	public static final String PARAMETER_SERVER_PORT = "server_port";
	
	@Override
	protected void onInitialize()
	{
		this.playerName = getParameter(LobbyScreen.PARAMETER_PLAYER_NAME);
		String serverIP = getParameter(LobbyScreen.PARAMETER_SERVER_IP);
		int serverPort = getParameter(LobbyScreen.PARAMETER_SERVER_PORT);
		
		this.playerAdapter = new PlayerAdapter(getContext(), new ArrayList<Player>());
		
		ListView listView = (ListView)findViewById(R.id.list_of_players);
		listView.setAdapter(this.playerAdapter);
		
		this.clientconnection = new ClientConnection(serverIP, serverPort, this);
		this.clientconnection.start();
		
		try
		{
			this.datagramCommunication = new DatagramCommunication(this);
			this.datagramCommunication.start();
		}
		catch (Exception e)
		{
			finish();
			showToast("ERROR CREATING CONNECTION");
		}
	}
	
	private void processPlayerInfo(PlayerInfo playerInfo)
	{
		this.player = new Player(playerInfo.id, playerInfo.name, playerInfo.color);
		this.udpPort = playerInfo.udpPort;
	}
	
	private void processPlayersList(final PlayersList registeredPlayers)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				refreshPlayerList(registeredPlayers.players);
			}
		});
	}
	
	private void refreshPlayerList(Player[] players)
	{
		ProgressBar progressBar = findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		
		this.playerAdapter.clear();
		
		for (Player player : players)
		{
			this.playerAdapter.add(player);
		}
	}
	
	private void processStartGame(StartGame startGame)
	{
		List<Player> enemies = getEnemyPlayers(this.player, startGame.players);
		
		GameScreen gameScreen = new GameScreen();
		gameScreen.setParameter(GameScreen.PARAMETER_PLAYER, this.player);
		gameScreen.setParameter(GameScreen.PARAMETER_ENEMIES, enemies);
		gameScreen.setParameter(GameScreen.PARAMETER_CONNECTION_TCP, this.clientconnection);
		gameScreen.setParameter(GameScreen.PARAMETER_CONNECTION_UDP, this.datagramCommunication);
		gameScreen.setParameter(GameScreen.PARAMETER_SERVER_UDP_PORT, this.udpPort);
		gameScreen.setParameter(GameScreen.PARAMETER_LAPS, startGame.laps);
		openFragment(gameScreen);
	}
	
	private List<Player> getEnemyPlayers(Player player, List<Player> players)
	{
		List<Player> result = new ArrayList<Player>();
		
		for (Player currentPlayer : players)
		{
			if (currentPlayer.id != player.id)
			{
				result.add(currentPlayer);
			}
		}
		
		return result;
	}
	
	@Override
	protected void onClose()
	{
		this.clientconnection.close();
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_lobby;
	}
	
	@Override
	public void onConnect()
	{
		ConnectionUtils.send(this.clientconnection, Messages.PlayerConnect.create(this.playerName, this.datagramCommunication.getLocalPort()));
	}
	
	@Override
	public void onErrorConnecting()
	{
		finish();
		showToast("ERROR CONNECTING");
	}
	
	@Override
	public void onDisconnect()
	{
		finish();
		showToast("DISCONNECCTED");
	}
	
	@Override
	public void onReceive(byte[] message)
	{
		MessageReader reader = new MessageReader(message);
		byte code = reader.getByte();
		
		switch (code)
		{
			case Messages.PlayerInfo.CODE:
				processPlayerInfo(new PlayerInfo(reader));
				break;
			
			case Messages.PlayersList.CODE:
				processPlayersList(new PlayersList(reader));
				break;
			
			case Messages.StartGame.CODE:
				processStartGame(new StartGame(reader));
				break;
		}
	}
	
	@Override
	public void onReceive(InetAddress address, int port, byte[] message)
	{
	}
}