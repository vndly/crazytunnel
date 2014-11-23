package com.mauriciotogneri.crazytunnel.screens;

import android.opengl.GLSurfaceView;
import com.mauriciotogneri.crazytunnel.R;
import com.mauriciotogneri.crazytunnel.activities.BaseFragment;
import com.mauriciotogneri.crazytunnel.engine.Game;

public class GameScreen extends BaseFragment
{
	private Game game;
	private GLSurfaceView screen;
	
	@Override
	protected void onInitialize()
	{
		// this.game = new Game(this);
		
		// CustomSurfaceView surfaceView = findViewById(R.id.glSurface);
		// surfaceView.setRenderer(new Renderer(this.game, this, this.screen));
	}
	
	@Override
	protected int getLayoutId()
	{
		return R.layout.screen_game;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		if (this.game != null)
		{
			this.game.resume();
		}
		
		if (this.screen != null)
		{
			this.screen.onResume();
		}
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		if (this.game != null)
		{
			this.game.pause(getBaseActivity().isFinishing());
		}
		
		if (this.screen != null)
		{
			this.screen.onPause();
		}
	}
	
	@Override
	public void onDestroy()
	{
		if (this.game != null)
		{
			this.game.stop();
		}
		
		super.onDestroy();
	}
}