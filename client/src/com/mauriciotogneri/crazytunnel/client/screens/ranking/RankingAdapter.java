package com.mauriciotogneri.crazytunnel.client.screens.ranking;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;

public class RankingAdapter extends ArrayAdapter<RankingRow>
{
	private final LayoutInflater inflater;
	
	public RankingAdapter(Context context, List<RankingRow> list)
	{
		super(context, R.layout.row_ranking, list);
		
		this.inflater = LayoutInflater.from(context);
	}
	
	@Override
	public View getView(int position, View originalView, ViewGroup parent)
	{
		View convertView = originalView;
		RankingRow rankingRow = getItem(position);
		
		if (convertView == null)
		{
			convertView = this.inflater.inflate(R.layout.row_ranking, parent, false);
		}
		
		convertView.setEnabled(false);
		convertView.setClickable(false);
		convertView.setOnClickListener(null);
		
		TextView playerPosition = (TextView)convertView.findViewById(R.id.position);
		playerPosition.setText(String.valueOf(position + 1));
		
		TextView name = (TextView)convertView.findViewById(R.id.player_name);
		name.setText(rankingRow.playerName);
		name.setTextColor(rankingRow.playerColor);
		
		TextView time = (TextView)convertView.findViewById(R.id.time);
		time.setText(RankingAdapter.formatTime(rankingRow.time));
		
		return convertView;
	}
	
	private static String formatTime(float time)
	{
		float restTime = time;
		
		int minutes = (int)(restTime / 60);
		restTime -= (minutes * 60);
		
		int seconds = (int)Math.floor(restTime);
		restTime -= seconds;
		
		return ((minutes < 10) ? ("0" + minutes) : minutes) + ":" + ((seconds < 10) ? ("0" + seconds) : seconds) + ":" + ((int)(restTime * 1000));
	}
}