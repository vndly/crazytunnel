package com.mauriciotogneri.crazytunnel.engine;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class CustomSurfaceView extends GLSurfaceView
{
	public CustomSurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
		setEGLContextClientVersion(2);
	}
}