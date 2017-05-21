
package cn.hxc.imgrecognition;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings.System;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Matrix;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.Buffer;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

public class MainActivity extends Activity implements OnTouchListener {
	private ImageButton button3;
	private ImageView iv_photo;
	private Matrix matrix = new Matrix();
	private int[] newpixels;
	private int[] pixels;
	private int width;
	private int height;

	// ��������
	private float old_x;
	private float old_y;
	private float new_x;
	private float new_y;
	private boolean isInRect;// �ж���ָ����ʱ����ھ��ο��λ��
	private boolean isInTop;
	private boolean isInBottom;
	private int newbottom;
	private int newtop;
	private Canvas canvas;
	private Paint paint;
	private Rect rect;
	private Bitmap paintbitMap;
	private int offset;
	private PreferencesService service;
	private int margain;

	private GuideUtil guideUtil = null;

	// static {
	// java.lang.System.loadLibrary("imageProcessing");
	// }
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// /**��ȡ�������湤�����ʵ��**/
		// guideUtil = GuideUtil.getInstance();
		// /**������������**/
		// guideUtil.initGuide(this, R.drawable.backgroud);
		// guideUtil.initGuide(MainActivity.this, R.drawable.backgroud);

		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.image_set);

		iv_photo = (ImageView) findViewById(R.id.imgView1);
		// Toast.makeText(context, text, duration)
		button3 = (ImageButton) findViewById(R.id.button3);
		// button3.setOnTouchListener(new btnOnTouchListener());
		service = new PreferencesService(this);
		Map<String, String> params = service.getPreferences("margain");// new
																		// Integer(params.get("margain")
		// if(params.get("margain")==null){
		// service.save("0","margain");
		// }
		SharedPreferences preferences = getSharedPreferences("set", MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putFloat("zoom", 1);
		editor.commit();

//		Intent intent = new Intent(this, processActivity.class);
//		startActivity(intent);

	}

	public final class btnOnTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				// �������ð���ʱ�ı���ͼƬ
				((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.set11_d));
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				// ���޸�Ϊ̧��ʱ������ͼƬ
				((ImageButton) v).setImageDrawable(getResources().getDrawable(R.drawable.set11));
			}
			return false;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		switch (event.getAction()) {// &MotionEvent.ACTION_MASK
		case MotionEvent.ACTION_DOWN:
			// old_x=event.getX();
			old_y = event.getY();
			if (old_y > newtop && old_y < newbottom) {
				isInRect = true;
			}
			if (old_y <= newtop) {
				isInTop = true;
			}
			if (old_y >= newbottom) {
				isInBottom = true;
			}
			// imageProcess.noequl("down", (int) old_y);
			break;
		case MotionEvent.ACTION_MOVE:
			onTouchMove(event);
			break;
		case MotionEvent.ACTION_UP:
			if (isInRect == true) {
				newbottom += offset;
				newtop += offset;
				isInRect = false;
			}
			if (isInTop == true) {
				newtop += offset;
				isInTop = false;
			}
			if (isInBottom == true) {
				newbottom += offset;
				isInBottom = false;
			}
			iv_photo.postInvalidate();
			break;
		}
		return true;
	}

	private void onTouchMove(MotionEvent event) {
		new_x = event.getX();
		new_y = event.getY();
		// imageProcess.noequl("move", (int) new_y);
		offset = (int) (new_y - old_y);
		Bitmap newbitMap = Bitmap.createBitmap(paintbitMap);
		canvas.drawBitmap(newbitMap, matrix, paint);
		rect = canvas.getClipBounds();

		if (isInRect == true) {
			// �жϱ߽�
			if (-offset >= newtop) {
				offset = (-newtop + 2);
			}
			if (offset > height - newbottom) {
				offset = height - newbottom - 1;
			}
			rect.bottom = (newbottom + offset);
			rect.top = (newtop + offset);

		} else if (isInTop == true) {
			if (-offset >= newtop) {
				offset = (-newtop + 2);
			}
			// �жϱ߽�
			if (offset > newbottom - newtop - 30) {
				offset = newbottom - newtop - 30;
			}
			rect.bottom = (int) (newbottom);
			rect.top = (int) (newtop + offset);

		} else if (isInBottom == true) {
			// �жϱ߽�
			if (-offset >= newbottom - newtop - 30) {
				offset = -(newbottom - newtop) + 30;
			}

			if (offset > height - newbottom) {
				offset = height - newbottom - 1;
			}
			rect.bottom = (int) (newbottom + offset);
			rect.top = (int) (newtop);

		}
		canvas.drawRect(rect, paint);
		iv_photo.postInvalidate();

	}

	public void takephoto(View v) {
		// Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Uri imageUri = Uri.fromFile(new
		// File(Environment.getExternalStorageDirectory(),
		// "workupload.jpg"));
		// // ָ����Ƭ����·����SD������workupload.jpgΪһ����ʱ�ļ���ÿ�����պ����ͼƬ���ᱻ�滻
		// cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
		// startActivityForResult(cameraIntent, 1);

		Intent intent = new Intent(this, takePhoto.class);
		startActivity(intent);

		// Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//
		// ����android�Դ��������
		// startActivityForResult(intent, 1);
	}

	/*
	 * @Override protected void onActivityResult(int requestCode, int
	 * resultCode, Intent data) { // ��������ص��ô˷���
	 * super.onActivityResult(requestCode, resultCode, data); if (resultCode !=
	 * Activity.RESULT_OK) {// result is not correct return; } //
	 * �������ڱ��ص�ͼƬȡ������С����ʾ�ڽ����� ? Bitmap bitmap =
	 * BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() +
	 * "/workupload.jpg"); if (null != bitmap) { Map<String, String> params=
	 * service.getPreferences("margain");// new Integer(params.get("margain")
	 * 
	 * try{ int marg= new Integer(params.get("margain")); margain=marg%360;
	 * }catch(Exception e){ margain=0; service.save("0","margain");
	 * imageProcess.noequl("Exception=   ", margain); }
	 * 
	 * 
	 * 
	 * // �����������Ƕ�ͼƬ����һ���ı������ţ������Ϳ�����������ʾ������ int mScreenWidth =
	 * this.getResources().getDisplayMetrics().widthPixels; //�õ���Ļ��� float scale
	 * = bitmap.getWidth() /(mScreenWidth+50); paintbitMap =
	 * ImageThumbnail.PicZoom(bitmap, bitmap.getWidth() / scale,
	 * bitmap.getHeight() / scale,margain); // paintbitMap =
	 * bitmap.createBitmap(bitmap); imageProcess.noequl("90000000   ", margain);
	 * Bitmap newbitMap = Bitmap.createBitmap(paintbitMap); // ���������ͼƬ��ʾ�ڽ�����
	 * height = newbitMap.getHeight(); width = newbitMap.getWidth(); canvas =
	 * new Canvas(newbitMap); paint = new Paint(); paint.setColor(Color.YELLOW);
	 * paint.setStrokeWidth(2); paint.setStyle(Paint.Style.STROKE);
	 * 
	 * newbottom = newbitMap.getHeight() * 3 / 5; newtop = newbitMap.getHeight()
	 * * 2 / 5; canvas.drawBitmap(newbitMap, matrix, paint); rect =
	 * canvas.getClipBounds(); rect.bottom = (int) newbottom; rect.top = (int)
	 * newtop; canvas.drawRect(rect, paint);
	 * iv_photo.setVisibility(View.VISIBLE); iv_photo.setImageBitmap(newbitMap);
	 * iv_photo.setOnTouchListener(this);
	 * 
	 * // ����Bitmap�ڴ�ռ�ýϴ�������Ҫ�����ڴ棬����ᱨout of memory�쳣 bitmap.recycle();
	 * 
	 * } }
	 * 
	 * 
	 * public void recognize(View v) { try{ Bitmap screenBitmap = Bitmap
	 * .createBitmap(paintbitMap, 0, newtop, width, newbottom - newtop);
	 * 
	 * imageProcess imageprocess = new imageProcess(); // ת��Ϊ�Ҷ�ͼ // Bitmap
	 * bitmap = //
	 * BitmapFactory.decodeFile(Environment.getExternalStorageDirectory() // +
	 * "/workupload.jpg"); Bitmap greyBitmap =
	 * imageprocess.RGBToGrey(screenBitmap); // iv_2.setImageBitmap(greyBitmap);
	 * // iv_2.setImageBitmap(greyBitmap);
	 * 
	 * // Bitmap greyBitmap = imageprocess.greyToArray(bitmap); // �洢�Ҷ�ͼ File
	 * greyFile = new File(Environment.getExternalStorageDirectory(),
	 * "greyTemp.jpg"); if (greyFile.exists()) { greyFile.delete(); }
	 * FileOutputStream fileout = new FileOutputStream(greyFile);
	 * greyBitmap.compress(CompressFormat.JPEG, 100, fileout); fileout.flush();
	 * fileout.close();
	 * 
	 * Intent intent = new Intent(this, processActivity.class);
	 * startActivity(intent); } catch(Exception e){ Toast.makeText(this, "��������",
	 * 1).show(); } }
	 */
	public void setPrefer(View v) {
		Intent intent = new Intent(this, set.class);
		startActivity(intent);
	}

}
