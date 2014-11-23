package com.mauriciotogneri.crazytunnel.screens.lobby;

import java.util.List;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mauriciotogneri.crazytunnel.R;

public class ServerAdapter extends ArrayAdapter<BluetoothDevice>
{
	private final LayoutInflater inflater;
	
	public ServerAdapter(Context context, List<BluetoothDevice> list)
	{
		super(context, R.layout.row_server, list);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		BluetoothDevice device = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.row_server, parent, false);
		}
		
		TextView name = (TextView)convertView.findViewById(R.id.server_name);
		name.setText(device.getName() + " - " + device.getAddress());
		
		return convertView;
	}
}