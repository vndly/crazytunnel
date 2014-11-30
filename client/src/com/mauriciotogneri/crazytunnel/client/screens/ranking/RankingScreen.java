package com.mauriciotogneri.crazytunnel.client.screens.ranking;

import java.util.ArrayList;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.client.screens.game.GameScreen;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;

public class RankingScreen extends BaseFragment
{
	private RankingAdapter rankingAdapter;
	
	public static final String PARAMETER_GAME_SCREEN = "game_screen";
	
	@Override
	protected void onInitialize()
	{
		final GameScreen gameScreen = getParameter(RankingScreen.PARAMETER_GAME_SCREEN);
		
		this.rankingAdapter = new RankingAdapter(getContext(), new ArrayList<RankingRow>());
		
		ListView listView = (ListView)findViewById(R.id.ranking_list);
		listView.setAdapter(this.rankingAdapter);
		
		final Button ready = findViewById(R.id.ready);
		ready.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				gameScreen.playerReady();
				ready.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	public void updateRankingList(RankingRow[] rankingList, boolean enableReady)
	{
		this.rankingAdapter.clear();
		
		for (RankingRow ranking : rankingList)
		{
			this.rankingAdapter.add(ranking);
		}
		
		if (enableReady)
		{
			Button ready = findViewById(R.id.ready);
			ready.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_ranking;
	}
}