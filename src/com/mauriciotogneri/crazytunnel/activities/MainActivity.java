package com.mauriciotogneri.crazytunnel.activities;

import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.screens.HomeScreen;

public class MainActivity extends BaseActivity
{
	@Override
	protected BaseFragment getHomeFragment()
	{
		return new HomeScreen();
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.activity_main;
	}
	
	@Override
	protected int getContainerId()
	{
		return R.id.fragment_container;
	}
	
	@Override
	protected void closeRequested()
	{
		finish();
	}
}