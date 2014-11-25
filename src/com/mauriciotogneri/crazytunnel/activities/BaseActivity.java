package com.mauriciotogneri.crazytunnel.activities;

import java.util.Stack;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity
{
	private BaseFragment homeFragment;
	private final Stack<BaseFragment> fragments = new Stack<BaseFragment>();
	
	@Override
	protected final void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		super.onCreate(savedInstanceState);
		
		setContentView(getLayoutId());
		
		this.homeFragment = getHomeFragment();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(getContainerId(), this.homeFragment);
		transaction.show(this.homeFragment);
		transaction.commit();
		
		onInitialize();
	}
	
	protected abstract BaseFragment getHomeFragment();
	
	protected abstract int getLayoutId();
	
	protected abstract int getContainerId();
	
	protected void onInitialize()
	{
	}
	
	protected void closeRequested()
	{
	}
	
	protected void onClose()
	{
	}
	
	public Vibrator getVibrator()
	{
		return (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
	}
	
	protected void openFragment(BaseFragment fragment)
	{
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.add(getContainerId(), fragment);
		transaction.hide(getCurrentFragment());
		transaction.commit();
		
		this.fragments.push(fragment);
	}
	
	protected void closeFragment()
	{
		if (!this.fragments.isEmpty())
		{
			FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
			BaseFragment closingFragment = this.fragments.pop();
			closingFragment.finish();
			transaction.remove(closingFragment);
			
			BaseFragment previous = getCurrentFragment();
			transaction.show(previous);
			transaction.commit();
			previous.onActivate();
		}
	}
	
	private BaseFragment getCurrentFragment()
	{
		return this.fragments.isEmpty() ? this.homeFragment : this.fragments.peek();
	}
	
	public BaseFragment getPreviousFragment()
	{
		BaseFragment result = null;
		
		if (this.fragments.size() > 1)
		{
			result = this.fragments.get(this.fragments.size() - 2);
		}
		else
		{
			result = this.homeFragment;
		}
		
		return result;
	}
	
	protected void showToast(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_LONG).show();
	}
	
	protected void showToast(int resourceId)
	{
		Toast.makeText(this, resourceId, Toast.LENGTH_LONG).show();
	}
	
	@Override
	public final void onBackPressed()
	{
		if (!this.fragments.isEmpty())
		{
			BaseFragment fragment = getCurrentFragment();
			
			if (fragment.allowBack())
			{
				closeFragment();
			}
		}
		else
		{
			closeRequested();
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		
		onClose();
	}
}