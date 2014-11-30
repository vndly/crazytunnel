package com.mauriciotogneri.crazytunnel.client.screens.ranking;

import java.util.ArrayList;
import android.widget.ListView;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.common.objects.RankingRow;

public class RankingScreen extends BaseFragment
{
	private RankingAdapter rankingAdapter;
	
	@Override
	protected void onInitialize()
	{
		this.rankingAdapter = new RankingAdapter(getContext(), new ArrayList<RankingRow>());
		
		ListView listView = (ListView)findViewById(R.id.ranking_list);
		listView.setAdapter(this.rankingAdapter);
	}
	
	public void updateRankingList(RankingRow[] rankingList)
	{
		this.rankingAdapter.clear();
		
		for (RankingRow ranking : rankingList)
		{
			this.rankingAdapter.add(ranking);
		}
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_ranking;
	}
}