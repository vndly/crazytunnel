package com.mauriciotogneri.crazytunnel.client.screens.lobby;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.common.objects.Player;

public class PlayerAdapter extends ArrayAdapter<Player>
{
	private final LayoutInflater inflater;
	
	public PlayerAdapter(Context context, List<Player> list)
	{
		super(context, R.layout.row_player, list);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		Player player = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.row_player, parent, false);
		}
		
		TextView name = (TextView)convertView.findViewById(R.id.player_name);
		name.setText(player.name);
		name.setTextColor(player.color);
		
		return convertView;
	}
}