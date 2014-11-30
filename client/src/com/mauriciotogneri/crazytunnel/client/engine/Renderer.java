package com.mauriciotogneri.crazytunnel.client.engine;

import java.nio.FloatBuffer;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import com.mauriciotogneri.crazytunnel.client.R;
import com.mauriciotogneri.crazytunnel.client.input.InputEvent;
import com.mauriciotogneri.crazytunnel.client.input.InputManager;
import com.mauriciotogneri.crazytunnel.client.util.FileUtils;
import com.mauriciotogneri.crazytunnel.client.util.ShaderUtils;

public class Renderer implements android.opengl.GLSurfaceView.Renderer
{
	public int width = 0;
	public int height = 0;
	
	private long startTime;
	
	private final Game game;
	private final Context context;
	
	private final float[] modelMatrix = new float[16];
	private final float[] projectionMatrix = new float[16];
	private final float[] finalMatrix = new float[16];
	
	private int matrixLocation;
	private int positionLocation;
	private int colorLocation;
	
	// input
	private final InputEvent input = new InputEvent();
	private final Object inputLock = new Object();
	
	// state
	private RendererStatus state = null;
	private final Object stateLock = new Object();
	
	public static final int RESOLUTION_X = 100;
	public static final int RESOLUTION_Y = 60;
	
	// renderer status
	private enum RendererStatus
	{
		RUNNING, IDLE, PAUSED, FINISHED
	}
	
	public Renderer(Game game, Context context, GLSurfaceView screen)
	{
		this.game = game;
		this.context = context;
		this.startTime = System.nanoTime();
		
		screen.setOnTouchListener(new InputManager()
		{
			@Override
			public void onPress(float x, float y)
			{
				processInput(getScreenX(x), true);
			}
			
			@Override
			public void onRelease(float x, float y)
			{
				processInput(getScreenX(x), false);
			}
		});
	}
	
	private float getScreenX(float x)
	{
		return ((x / this.width) * Renderer.RESOLUTION_X);
	}
	
	private void processInput(float x, boolean pressed)
	{
		synchronized (this.inputLock)
		{
			if (pressed)
			{
				this.input.press(x, Renderer.RESOLUTION_X);
			}
			else
			{
				this.input.release(x, Renderer.RESOLUTION_X);
			}
		}
	}
	
	private void update(double delta)
	{
		synchronized (this.inputLock)
		{
			this.game.update(delta, this.input, this);
		}
	}
	
	public void clearScreen(Camera camera)
	{
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		Matrix.orthoM(this.projectionMatrix, 0, camera.x, camera.x + camera.width, camera.y, camera.y + camera.height, -1f, 1f);
	}
	
	public void renderShape(FloatBuffer vertexData, float x, float y, int color, float alpha, float scaleX, float scaleY, int length)
	{
		Matrix.setIdentityM(this.modelMatrix, 0);
		Matrix.translateM(this.modelMatrix, 0, x, y, 0);
		Matrix.scaleM(this.modelMatrix, 0, scaleX, scaleY, 1f);
		// TODO: ROTATE
		
		Matrix.multiplyMM(this.finalMatrix, 0, this.projectionMatrix, 0, this.modelMatrix, 0);
		GLES20.glUniformMatrix4fv(this.matrixLocation, 1, false, this.finalMatrix, 0);
		
		// -----------------------------------
		
		vertexData.position(0);
		// 2: position_component_count
		// 8: position_component_count * bytes_per_float => (2 * 4)
		GLES20.glVertexAttribPointer(this.positionLocation, 2, GLES20.GL_FLOAT, false, 8, vertexData);
		GLES20.glEnableVertexAttribArray(this.positionLocation);
		
		float red = Color.red(color) / 255f;
		float green = Color.green(color) / 255f;
		float blue = Color.blue(color) / 255f;
		
		GLES20.glUniform4f(this.colorLocation, red, green, blue, alpha);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, length);
	}
	
	@Override
	public void onDrawFrame(GL10 unused)
	{
		RendererStatus status = null;
		
		synchronized (this.stateLock)
		{
			status = this.state;
		}
		
		if (status == RendererStatus.RUNNING)
		{
			long currentTime = System.nanoTime();
			double delta = (currentTime - this.startTime) / 1E9d;
			this.startTime = currentTime;
			
			// FPS.log(currentTime);
			
			update(delta);
		}
		else if ((status == RendererStatus.PAUSED) || (status == RendererStatus.FINISHED))
		{
			synchronized (this.stateLock)
			{
				this.state = RendererStatus.IDLE;
				this.stateLock.notifyAll();
			}
		}
	}
	
	@Override
	public void onSurfaceCreated(GL10 screen, EGLConfig config)
	{
		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glBlendFunc(GLES20.GL_ONE, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		
		GLES20.glClearColor(0.9f, 0.9f, 0.9f, 1f);
		
		String vertexShaderSource = FileUtils.readTextFile(this.context, R.raw.vertex_shader);
		String fragmentShaderSource = FileUtils.readTextFile(this.context, R.raw.fragment_shader);
		
		int program = ShaderUtils.linkProgram(vertexShaderSource, fragmentShaderSource);
		GLES20.glUseProgram(program);
		
		this.matrixLocation = GLES20.glGetUniformLocation(program, "u_Matrix");
		this.colorLocation = GLES20.glGetUniformLocation(program, "u_Color");
		this.positionLocation = GLES20.glGetAttribLocation(program, "a_Position");
	}
	
	@Override
	public void onSurfaceChanged(GL10 screen, int width, int height)
	{
		this.width = width;
		this.height = height;
		
		GLES20.glViewport(0, 0, width, height);
		
		Matrix.orthoM(this.projectionMatrix, 0, 0, Renderer.RESOLUTION_X, 0, Renderer.RESOLUTION_Y, -1f, 1f);
		
		synchronized (this.stateLock)
		{
			this.state = RendererStatus.RUNNING;
		}
		
		this.startTime = System.nanoTime();
		this.game.setRenderer(this);
	}
	
	public void pause(boolean finishing)
	{
		synchronized (this.stateLock)
		{
			if (this.state == RendererStatus.RUNNING)
			{
				if (finishing)
				{
					this.state = RendererStatus.FINISHED;
				}
				else
				{
					this.state = RendererStatus.PAUSED;
				}
				
				while (true)
				{
					try
					{
						this.stateLock.wait();
						break;
					}
					catch (Exception e)
					{
					}
				}
			}
		}
	}
}