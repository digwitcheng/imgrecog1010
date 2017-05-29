package cn.hxc.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.FrameLayout;

import cn.hxc.view.rectView;

public class mySurfaceView extends FrameLayout {
	private SurfaceView surfaceView;
	private rectView imageView;
	private int maskWidth;
	private int maskHeight;
	private int screenWidth;
	private int screenHeight;
	public mySurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		surfaceView = new SurfaceView(context);
		imageView = new rectView(context);
		this.addView(surfaceView,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		this.addView(imageView,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		
		Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenHeight = display.getHeight();
		screenWidth = display.getWidth();
		
	}
	

	

	
}
