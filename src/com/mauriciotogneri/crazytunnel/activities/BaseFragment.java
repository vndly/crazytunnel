package com.mauriciotogneri.crazytunnel.activities;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment
{
	private View layout;
	private BaseActivity baseActivity;
	
	@Override
	public final void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		this.baseActivity = (BaseActivity)activity;
	}
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		this.layout = inflater.inflate(getLayoutId(), container, false);
		
		onInitialize();
		onActivate();
		
		return this.layout;
	}
	
	protected abstract int getLayoutId();
	
	protected void onInitialize()
	{
	}
	
	protected void onActivate()
	{
	}
	
	protected void onClose()
	{
	}
	
	public Context getContext()
	{
		return this.baseActivity;
	}
	
	public BaseActivity getBaseActivity()
	{
		return this.baseActivity;
	}
	
	protected BaseFragment getPreviousFragment()
	{
		return this.baseActivity.getPreviousFragment();
	}
	
	protected void runOnUiThread(Runnable runnable)
	{
		this.baseActivity.runOnUiThread(runnable);
	}
	
	@SuppressWarnings("unchecked")
	protected <Type> Type getParameter(String name)
	{
		Bundle parameters = getArguments();
		
		return (Type)parameters.get(name);
	}
	
	@SuppressWarnings("unchecked")
	protected <Type> Type findViewById(int id)
	{
		return (this.layout != null) ? (Type)this.layout.findViewById(id) : null;
	}
	
	protected void openFragment(BaseFragment fragment)
	{
		this.baseActivity.openFragment(fragment);
	}
	
	protected void showToast(String text)
	{
		this.baseActivity.showToast(text);
	}
	
	protected void showToast(int resourceId)
	{
		this.baseActivity.showToast(resourceId);
	}
	
	protected boolean allowBack()
	{
		return true;
	}
	
	protected void finish()
	{
		onClose();
	}
	
	protected void close()
	{
		this.baseActivity.closeFragment();
	}
}