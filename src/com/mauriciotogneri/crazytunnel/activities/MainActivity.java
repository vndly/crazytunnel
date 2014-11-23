package com.mauriciotogneri.crazytunnel.activities;

import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.screens.HomeScreen;
import com.mauriciotogneri.crazytunnel.shapes.Preferences;

public class MainActivity extends BaseActivity
{
	@Override
	protected void onInitialize()
	{
		Preferences.initialize(this);
	}
	
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