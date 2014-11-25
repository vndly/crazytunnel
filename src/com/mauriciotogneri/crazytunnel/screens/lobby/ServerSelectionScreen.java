package com.mauriciotogneri.crazytunnel.screens.lobby;

import java.util.ArrayList;
import android.bluetooth.BluetoothDevice;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.mauriciotogneri.bluetooth.connection.scan.DeviceScanner;
import com.mauriciotogneri.bluetooth.connection.scan.ScannerManager;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;

public class ServerSelectionScreen extends BaseFragment implements DeviceScanner
{
	private String playerName = "";
	private ScannerManager scannerManager;
	private ServerAdapter serverAdapter;
	
	public static final String PARAMETER_PLAYER_NAME = "player_name";
	
	@Override
	protected void onInitialize()
	{
		this.playerName = getParameter(ServerSelectionScreen.PARAMETER_PLAYER_NAME);
		
		this.serverAdapter = new ServerAdapter(getContext(), new ArrayList<BluetoothDevice>());
		
		ListView listView = (ListView)findViewById(R.id.list_of_servers);
		listView.setAdapter(this.serverAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				BluetoothDevice device = (BluetoothDevice)parent.getItemAtPosition(position);
				serverSelected(device);
			}
		});
		
		this.scannerManager = new ScannerManager(getContext(), this);
		this.scannerManager.scan();
	}
	
	private void serverSelected(BluetoothDevice device)
	{
		LobbyClientScreen lobbyClient = new LobbyClientScreen();
		lobbyClient.setParameter(ServerSelectionScreen.PARAMETER_PLAYER_NAME, this.playerName);
		lobbyClient.setParameter(LobbyClientScreen.PARAMETER_SERVER, device);
		openFragment(lobbyClient);
	}
	
	private void addServer(BluetoothDevice device)
	{
		ProgressBar progressBar = findViewById(R.id.progressBar);
		progressBar.setVisibility(View.GONE);
		
		this.serverAdapter.add(device);
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_server_selection;
	}
	
	@Override
	protected void onClose()
	{
		this.scannerManager.stop();
	}
	
	@Override
	public void onDeviceDiscovered(final BluetoothDevice device)
	{
		runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				addServer(device);
			}
		});
	}
}