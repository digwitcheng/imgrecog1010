package cn.hxc.Activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.hxc.Camera.CameraUtil;
import cn.hxc.imgrecognition.R;
import cn.hxc.ImageUtil.imageProcess;
import cn.hxc.view.verticalSeekBar;

public class takePhoto extends Activity {
	public static final String TAG = "takePhoto";
	private Button btn_takephoto;
	private SurfaceView surfaceView;
	private EditText rectView;
	private Camera camera;
	private boolean preview;
	private Activity activity;
	private View nextLayout;
	private View fisrtLayout;
	private Parameters parameters;
	private int zoomValue;
	private int oldZoomValue;

	private verticalSeekBar seekBar;
	private ImageView seekBar_imageview;
	private int displayOrientation;

	private static int sWidth;
	private static int sHight;
	static int rectImage_y;
	static boolean isWriteRecogize;

	// ��������
	private float mScreenWidth;
	private float mScreenHeight;
	private int width;
	private int height;

	// ��������
	private float old_x;
	private float old_y;
	private float new_x;
	private float new_y;
	private float old_x1;
	private float old_y1;
	private float new_x1;
	private float new_y1;
	// private boolean isInRect;// �ж���ָ����ʱ����ھ��ο��λ��
	// private boolean isInTop;
	// private boolean isInBottom;
	private boolean isLeft;
	private boolean isRight;
	private boolean isVerticalMove = false;
	private boolean isDown;
	private Rect rect;
	private Canvas canvas;
	private Paint paint;
	private int margain;

	// ˫�ֲ���
	private int topBord;
	private int leftBord;
	// private int leftBord;
	private int offsetTop;
	private int offsetLeft;

	private LinearLayout.LayoutParams lp;
	// private LinearLayout.LayoutParams lpLeft;
	// private LinearLayout.LayoutParams lpRight;

	private SharedPreferences preferences;
	static final float scaleLeft = (float)1.0/6;
	static final float scaleTop = (float) 5.0 / 24;

	private static int zoom;

	private CountDownTimer timer;
	private boolean isMove = false;
	private boolean isOnFocusAgain = false;

	private final float minDistance = 10;
	private static final float maxLeftScale = (float) (2.0 / 5);
	private static final int minLeft = 3;
	private static final float maxBottomSacle = (float) (1.0 / 2);
	private static final float minTopSacle = (float) (1.0 / 4);
	private static final int minRectHeight = 20;

	private Rect recognizeRect;
	private Drawable picDrawable;

	private View view_focus = null;
	//private PreviewFrameLayout frameLayout = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Window window = getWindow();
		requestWindowFeature(Window.FEATURE_NO_TITLE);// ȥ������
		window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);// ����ȫ��
		// window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.takephoto);

		WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		mScreenWidth = outMetrics.widthPixels;
		mScreenHeight = outMetrics.heightPixels;
		height = (int) mScreenHeight;
		width = (int) mScreenWidth;
		rectView = (EditText) findViewById(R.id.rectView);
		btn_takephoto = (Button) findViewById(R.id.btn_takephoto);

		imageProcess.noequl(scaleLeft + "/", 0);
		imageProcess.noequl(scaleTop + "/", 0);

		preferences = getSharedPreferences("set", MODE_PRIVATE);
		if (!preferences.contains("positionLeft")) {
			savePosition("positionLeft", scaleLeft);
			savePosition("positionTop", scaleTop);
		}
		// imageProcess.noequl("positionLeft=", getBottomPosition());
		initTakephoto();

	}

	void savePosition(String string, float scale) {
		Editor editor = preferences.edit();
		editor.putFloat(string, scale);
		editor.commit();
	}

	float getLeftPosition() {
		return preferences.getFloat("positionLeft", scaleLeft);
	}

	float getBottomPosition() {
		return preferences.getFloat("positionTop", scaleTop);
	}

	int px(float pix) {

		return (int) ((pix + 0.5) * 1.5);
	}

	public void initTakephoto() {
		paint = new Paint();
		paint.setColor(Color.YELLOW);
		paint.setStrokeWidth(2);
		paint.setStyle(Paint.Style.STROKE);
		paint.setPathEffect(new DashPathEffect(new float[] { 3, 2 }, 0));
		canvas = new Canvas();
		Path path = new Path();
		path.moveTo(10, 10);
		path.lineTo(10, 400);
		canvas.drawPath(path, paint);

		 leftBord = (int) (getLeftPosition() * mScreenWidth);
		// rightBord = (int) (getRightPosition() * mScreenWidth);
		 topBord = (int) (getBottomPosition() * mScreenHeight);
		 
//		leftBord = width / 6;
//		topBord = (int) (height * minTopSacle - height / 24);
		imageProcess.noequl("getBottomPosition=", getBottomPosition());

		lp = new LinearLayout.LayoutParams(width - 2 * leftBord, (int) ((height * minTopSacle - topBord) * 2));
		lp.setMargins(leftBord, topBord, 0, 0);
		rectView.setLayoutParams(lp);

		// rectView.setLayoutParams(new
		// LinearLayout.LayoutParams((int)mScreenWidth, (int)mScreenHeight/10));
		isWriteRecogize = false;
		// (paintbitMap, 0, newtop, screenWidth, newbottom - newtop);
		activity = this;
		surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
		nextLayout = this.findViewById(R.id.nextlayout);
		fisrtLayout = this.findViewById(R.id.firstLayout);
		/* ��������Surface��ά���Լ��Ļ����������ǵȴ���Ļ����Ⱦ���潫�������͵��û���ǰ */
		surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		// surfaceView.getHolder().setFixedSize(176, 144); // ���÷ֱ���
		surfaceView.setFocusable(true);
		surfaceView.getHolder().setKeepScreenOn(true);
		surfaceView.getHolder().addCallback(new SufaceListener());// ��Ӽ���
		// ��������ʼ������
		// seekBar_imageview = (ImageView)
		// findViewById(R.id.seekbar_imageview_id);
		// seekBar = (verticalSeekBar) findViewById(R.id.seekbar_btn_id);
		// seekBar.setMax(100);
		// seekBar.setOnSeekBarChangeListener(new onseekBarListener());

		timer = new CountDownTimer(200000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				if (isOnFocusAgain) {
					timer.cancel();
					isOnFocusAgain = false;
				}

			}

			@Override
			public void onFinish() {
				if (isMove == false) {
					if (!isOnFocusAgain) {
						if (camera != null) {
							btn_takephoto.setEnabled(false);
							camera.takePicture(null, null, mJpegPictureCallback);
						}
					}
				} else {
					timer.cancel();
				}
			}
		};
	}

	@Override
	public void onBackPressed() {
		if (timer != null) {
			timer.cancel();
		}
		this.finish();
		super.onBackPressed();
	}

	private final class SufaceListener implements SurfaceHolder.Callback {
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
			// ʵ���Զ��Խ�
			if (camera != null) {
				camera.cancelAutoFocus();// ֻ�м�������һ�䣬�Ż��Զ��Խ���
				camera.autoFocus(new AutoFocusCallback() {
					@Override
					public void onAutoFocus(boolean success, final Camera camera) {
						if (success) {
							initCamera();// ʵ������Ĳ�����ʼ��
							if (parameters.getSupportedFocusModes()
									.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
								parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

							}
							if (timer != null) {
								timer.cancel();
							}
							timer.start();
							isOnFocusAgain = true;
						}
					}

				});

			}

		}

		@SuppressWarnings("deprecation")
		@Override
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera = Camera.open();// ������ͷ
				initCamera();
				camera.setPreviewDisplay(holder);
				nextLayout.setVisibility(ViewGroup.GONE);
				fisrtLayout.setVisibility(ViewGroup.VISIBLE);
				camera.startPreview();
				preview = true;
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			}
		}

		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			if (camera != null) {
				if (preview)
					camera.stopPreview();
				camera.release();
				camera = null;
			}
		}
	}

	/** ��¼��������Ƭģʽ���ǷŴ���С��Ƭģʽ */

	private static final int MODE_INIT = 0;
	/** �Ŵ���С��Ƭģʽ */
	private static final int MODE_POINTER = 1;
	private int mode = MODE_INIT;// ��ʼ״̬

	/** ���ڼ�¼����ͼƬ�ƶ�������λ�� */

	private float startDis;
	private float endDis;

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		/** ͨ�������㱣������λ MotionEvent.ACTION_MASK = 255 */
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// ��ָѹ����Ļ
		case MotionEvent.ACTION_DOWN:
			timer.cancel();
			// task.cancel();
			isMove = true;
			mode = MODE_INIT;

			isDown = true;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			old_x = event.getX(0);
			old_y = event.getY(0);
			old_x1 = event.getX(1);
			old_y1 = event.getY(1);
			
			// �Ƴ�token����ΪmZoomSeekBar����ʱ����
			// mZoomSeekBar.setVisibility(View.VISIBLE);
			mode = MODE_POINTER;
			/** ����������ָ��ľ��� */
			startDis = distance(event);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == MODE_POINTER) {
				// ֻ��ͬʱ�����������ʱ���ִ��
				if (event.getPointerCount() < 2) {
					return true;
				}
				onTouchMove(event);
				// startDis=endDis;
			}
			break;
		// ��ָ�뿪��Ļ
		case MotionEvent.ACTION_UP:
			isMove = false;
			if (mode == MODE_POINTER) {
				topBord -= offsetTop / 2;
				imageProcess.noequl("topBord b��=", topBord);
				leftBord -= offsetLeft / 2;

			}
			 savePosition("positionLeft", (float) leftBord / mScreenWidth);
			 savePosition("positionTop", (float) topBord /
			 mScreenHeight);

			break;
		}

		return super.onTouchEvent(event);
	}

	private void onTouchMove(MotionEvent event) {

		endDis = distance(event);
		//if ((endDis - startDis)*(endDis - startDis) > 8) {
			new_x = event.getX(0);
			new_y = event.getY(0);
			new_x1 = event.getX(1);
			new_y1 = event.getY(1);
			float distance_x = (float) Math.sqrt((old_x - new_x) * (old_x - new_x))
					+ (float) Math.sqrt((old_x1 - new_x1) * (old_x1 - new_x1));
			float distance_y = (float) Math.sqrt((old_y - new_y) * (old_y - new_y))
					+ (float) Math.sqrt((old_y1 - new_y1) * (old_y1 - new_y1));
			// if(distance(event) > minDistance) {
			if (isDown == true) {
                isVerticalMove = distance_x < distance_y;
				isDown = false;
			}
			if (isVerticalMove) {
				offsetLeft = 0;
				offsetTop = (int) ((endDis - startDis)/2);
				if(offsetTop/2>topBord-height/8){
					offsetTop=(topBord-height/8)*2;
				}
				if(-offsetTop/2>-topBord+height*minTopSacle-15){
					offsetTop=(int) (topBord-height*minTopSacle+15)*2;
				}
				
			} else {
				offsetTop = 0;
				offsetLeft = (int) ((endDis - startDis)/2);
				if(leftBord - offsetLeft / 2<5){
				  offsetLeft=(leftBord -5)*2;
				}
				if(leftBord - offsetLeft / 2>2*width/5){
					offsetLeft=(leftBord-2*width/5)*2;
				}
				
			}

			// (int) mScreenHeight / 4 + (int) mScreenHeight / 12
			lp = new LinearLayout.LayoutParams(width - 2 * leftBord + offsetLeft,
					(int) ((height * minTopSacle - topBord) * 2 + offsetTop));
			lp.setMargins(leftBord - offsetLeft / 2, topBord - offsetTop / 2, 0, 0);
			rectView.setLayoutParams(lp);
		}

		// imageProcess.noequl("width=",width-2*newLeftBord );
		// imageProcess.noequl("height=", (int)
		// ((height*minTopSacle-topBord)*2+offsetTop));
		// imageProcess.noequl("disTop", topBord-offsetTop/2);
//	}

	// }

	/** ����������ָ��ľ��� */
	private float distance(MotionEvent event) {
		float dx = event.getX(1) - event.getX(0);
		float dy = event.getY(1) - event.getY(0);
		/** ʹ�ù��ɶ���������֮��ľ��� */
		return (float) Math.sqrt(dx * dx + dy * dy);
	}

	// ��������ĳ�ʼ������
	private void initCamera() {

		try {
			btn_takephoto.setEnabled(true);
			parameters = camera.getParameters();// ����ͷ�Ĳ���
			// List<int[]> range=parameters.getSupportedPreviewFpsRange();
			// parameters.setPreviewFrameRate(20);// ÿ��20֡
			// List<Integer> formerate= parameters.getSupportedPreviewFormats();
			// parameters.setPreviewFormat(formerate.get(formerate.size()/2));
			if (camera.getParameters().isZoomSupported()) {
				imageProcess.noequl("֧�ֱ佹 ", 1);
			}
			WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // ��ȡ��ǰ��Ļ����������
			Display display = wm.getDefaultDisplay(); // ��ȡ��Ļ��Ϣ��������
			// parameters.setPreviewSize(display.getWidth(), display.getHeight());
			// // ����

			Size picSize = CameraUtil.getInstance().getPictureSize(parameters
					.getSupportedPictureSizes(), 800);

			parameters.setPictureSize(picSize.width, picSize.height);
			parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);// 1�����Խ�
			// ��������Ԥ��
			setCameraDisplayOrientation(activity, Camera.CameraInfo.CAMERA_FACING_BACK, camera);
			camera.setParameters(parameters);
			camera.cancelAutoFocus();// 2���Ҫʵ���������Զ��Խ�����һ��������
		}catch (Exception e)
		{}
	}

	private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
		final double ASPECT_TOLERANCE = 0.05;
		double targetRatio = (double) w / h;
		if (sizes == null)
			return null;
		Size optimalSize = null;
		double minDiff = Double.MAX_VALUE;
		int targetHeight = h;
		// Try to find an size match aspect ratio and size
		for (Size size : sizes) {
			double ratio = (double) size.width / size.height;
			if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
				continue;
			if (Math.abs(size.height - targetHeight) < minDiff) {
				optimalSize = size;
				minDiff = Math.abs(size.height - targetHeight);
			}
		}
		// Cannot find the one match the aspect ratio, ignore the requirement
		if (optimalSize == null) {
			minDiff = Double.MAX_VALUE;
			for (Size size : sizes) {
				if (Math.abs(size.height - targetHeight) < minDiff) {
					optimalSize = size;
					minDiff = Math.abs(size.height - targetHeight);
				}
			}
		}
		return optimalSize;
	}

	public void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360;// compensate the mirror
		} else {// back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		displayOrientation = result;
		camera.setDisplayOrientation(result);
	}

	public void printRecognize(View v) throws IOException {
		isWriteRecogize = false;
		int location[] = new int[2];
		rectView.getLocationOnScreen(location);
		imageProcess.noequl("location", location[0]);
		imageProcess.noequl("location", location[1]);
		rectImage_y = location[1];
		Intent intent = new Intent(this, processActivity.class);
		startActivity(intent);

	}

	public void writeRecognize(View v) {
		isWriteRecogize = true;
		int location[] = new int[2];
		rectView.getLocationOnScreen(location);
		imageProcess.noequl("location", location[0]);
		imageProcess.noequl("location", location[1]);
		rectImage_y = location[1];
		Intent intent = new Intent(this, processActivity.class);
		startActivity(intent);
	}

	public void takepicture(View v) {
		// nextLayout.setVisibility(ViewGroup.VISIBLE);
		// fisrtLayout.setVisibility(ViewGroup.GONE);
		if (timer != null) {
			timer.cancel();
		}
		if (camera != null) {// ����ǰ�жϣ��������������Ϊnull
			btn_takephoto.setEnabled(false);
			camera.takePicture(null, null, mJpegPictureCallback);
		}
		// camera.takePicture(null, null,null,
		// mJpegPictureCallback);
		// camera.startPreview();//�����յ��������������Ԥ���������ǲ����Ե�
		// camera.takePicture��������ڲ��������첽������Ƭ,��camera.takePicture����ִ���������ͷ���ܻ�û�д�������Ƭ
		// ����ֱ����camera.takePicture�����������camera.startPreview();�����ǲ��Ե�
		// ���������ʼ���գ������������ǣ����Ű���ȥ��
		/*
		 * null,��һ�������Ű���ȥ֮�󣬻������������еĻص�����
		 * null���ڶ��������ǵ�����ͷ�������Ƭ���ݷ�Ϊ��Ƭ��ԭʼ���ݣ�����Ƭ����ѹ��������ݣ��ڶ�����������ָ��Ƭ��ԭʼ����
		 * ����������������Ƭ����ѹ��������� new MyPictureCallback()����������ǵõ�����ͷ�����꣬��ѹ���������
		 */
		// ����ͷ���յ�ʱ���ǲ�����Ԥ���ģ���Ϊ����ͷ��ĳһ��ʱ��ֻ����һ�����飬�������պ��û��Ԥ�������ˡ�

	}

	public void comeBack(View v) {
//		nextLayout.setVisibility(ViewGroup.GONE);
//		fisrtLayout.setVisibility(ViewGroup.VISIBLE);
//		camera.startPreview();
		this.finish();
	}

	PictureCallback mJpegPictureCallback = new PictureCallback() {

		// Skipped 47 frames! The application may be doing too much work on its
		// main thread.
		@Override
		public void onPictureTaken(byte[] data, Camera arg1) {
			// TODO Auto-generated method stub
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length,options);

			BitmapFactory.Options opts=getOption(options);
			Bitmap bitmap1= BitmapFactory
					.decodeByteArray(data, 0, data.length,opts);
			preview = true;

			Size size1=camera.getParameters().getPreviewSize();
			Size size2=camera.getParameters().getPictureSize();

			if (bitmap1 != null) {

				FileOutputStream fos;
				Matrix matrix = new Matrix();
				matrix.postRotate(displayOrientation);
				Bitmap bitmap = Bitmap.createBitmap(bitmap1, 0, 0,
						bitmap1.getWidth(), bitmap1.getHeight(), matrix, false);

				float scaleX = bitmap.getWidth() / mScreenWidth;
				float scaleY = bitmap.getHeight() / mScreenHeight;
				recognizeRect = new Rect(leftBord, topBord, width - leftBord, (int) (2
						* minTopSacle * height - topBord));
				Bitmap rotaBitmap = Bitmap.createBitmap(bitmap,
						(int) (scaleX * recognizeRect.left),
						(int) (recognizeRect.top * scaleY),
						(int) (recognizeRect.width() * scaleX),
						(int) (recognizeRect.height() * scaleY));

				try {
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ File.separator + "KuaiDiBangShou");
					if (!file.exists()) {
						file.mkdirs();
					}
					fos = new FileOutputStream(file + File.separator
							+ "greyTemp.jpg");
					rotaBitmap.compress(CompressFormat.JPEG, 85, fos);
					fos.flush();
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				bitmap.recycle();
				bitmap=null;
				bitmap1.recycle();
				bitmap1=null;

			int location[] = new int[2];
			rectView.getLocationOnScreen(location);
			imageProcess.noequl("location", location[0]);
			imageProcess.noequl("location", location[1]);
			rectImage_y = location[1];
			timer.cancel();

			Intent intent = new Intent(takePhoto.this, processActivity.class);
			startActivity(intent);
			}
				else
				{
					// �������
					camera.stopPreview();
					camera.startPreview();
					btn_takephoto.setEnabled(true);
				}

		}

	};
	public BitmapFactory.Options getOption(BitmapFactory.Options opts){
		//2.Ϊλͼ����100K�Ļ���
		//BitmapFactory.Options opts=new BitmapFactory.Options();
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
//4.����ͼƬ���Ա����գ�����Bitmap���ڴ洢Pixel���ڴ�ռ���ϵͳ�ڴ治��ʱ���Ա�����
		opts.inPurgeable = true;
//5.����λͼ���ű���
//width��hight��Ϊԭ�����ķ�һ���ò�����ʹ��2����������,��Ҳ��С��λͼռ�õ��ڴ��С�����磬һ��//�ֱ���Ϊ2048*1536px��ͼ��ʹ��inSampleSizeֵΪ4�����������룬������Bitmap��СԼΪ//512*384px�����������ͼƬռ��12M���ڴ棬���ַ�ʽֻ��0.75M�ڴ�(����Bitmap����Ϊ//ARGB_8888)��

		Size size1=camera.getParameters().getPreviewSize();
//	 opts.outHeight=size1.height;
//	 opts.outWidth=size1.width;

		opts.inSampleSize =(int) (opts.outHeight/mScreenHeight);
//6.���ý���λͼ�ĳߴ���Ϣ
		opts.inInputShareable = true;
		opts.inJustDecodeBounds=false;
		return  opts;
	}
}
