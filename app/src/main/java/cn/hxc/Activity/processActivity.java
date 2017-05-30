package cn.hxc.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import cn.hxc.ImageUtil.ImageThumbnail;
import cn.hxc.ToolUtil.PreferencesService;
import cn.hxc.imgrecognition.ConnectedRect;
import cn.hxc.imgrecognition.R;
import cn.hxc.imgrecognition.RemoveNoise;
import cn.hxc.ImageUtil.imageProcess;
import cn.hxc.view.HXCButton;

public class processActivity extends Activity {

    static {
        System.loadLibrary("processActivity");
    }

    public native int[] removeNoise(int[] by, int width, int height);

    public native int callint( String num, String win2, String whi2, int flag, String model);

    public native int Recognize(int []by1,int w,int h,int flag);

    private EditText tv_num;
    private ImageView iv_photo;
    private Matrix matrix = new Matrix();
    private ImageView iv_2;
    private int[] newpixels;
    private int[] pixels;
    private int width;
    private int height;
    private PreferencesService service;
    private String content;
    private Bitmap greyBitmap;
    private int sorta[];
    private Bitmap greyBitmap1;
    private boolean isWriteRecogize;
    private int[] by;

    private boolean isOk;
    // 触摸方框
    private float mScreenWidth;
    private float mScreenHeight;
    private float old_x;
    private float old_y;
    private float new_x;
    private float new_y;
    private boolean isInRect;// 判断手指按下时相对于矩形框的位置
    private boolean isInTop;
    private boolean isInBottom;
    private int newbottom;
    private int newtop;
    private Canvas canvas;
    private Paint paint;
    private Rect rect;
    private Bitmap paintbitMap;

    private int offset;

    private SharedPreferences preferences;
    private RemoveNoise processUtil;

    private HXCButton phoneBtn;
    private HXCButton smsBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process);
        iv_photo = (ImageView) findViewById(R.id.imgView1);
        iv_2 = (ImageView) findViewById(R.id.imgView2);
        tv_num = (EditText) findViewById(R.id.tv_num);
        tv_num.setVisibility(View.GONE);
        WindowManager wm = (WindowManager) this.getSystemService(WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        mScreenWidth = outMetrics.widthPixels;
        mScreenHeight = outMetrics.heightPixels;
        isWriteRecogize = false;// takePhoto.isWriteRecogize;
        isOk = false;
        newtop = takePhoto.rectImage_y;
        newbottom = takePhoto.rectImage_y + 50;

        phoneBtn = (HXCButton) findViewById(R.id.phone_btn);
        smsBtn = (HXCButton) findViewById(R.id.sms_btn);
        phoneBtn.setButtonClickLisener(new HXCButton.ButtonClickLisener() {
            @Override
            public void myClick() {
                callphone();
            }
        });
        smsBtn.setButtonClickLisener(new HXCButton.ButtonClickLisener() {
            @Override
            public void myClick() {
                sendMsg();
            }
        });

        // dip（value）= (int) (px（value）/1.5 + 0.5)
        // imageProcess.noequl("newtop ", newtop);

        try {
            String filePath = Environment.getExternalStorageDirectory() + File.separator + "KuaiDiBangShou"
                    + File.separator + "Data";
            assetsDataToSD(filePath, "win2.dat");
            assetsDataToSD(filePath, "whi2.dat");
            assetsDataToSD(filePath, "num2");

            assetsDataToSD(filePath, "win_h.dat");
            assetsDataToSD(filePath, "whi_h.dat");
            assetsDataToSD(filePath, "num_h");



            String strNum = filePath + "//num2";
            String strWin2 = filePath + "//win2.dat";
            String strWhi2 = filePath + "//whi2.dat";
            String strModel = filePath + "//model14.dat";

            callint(strNum,strWin2,strWhi2,1,strModel);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            // imageProcess.noequl("win2.dat read fail", 111);
        }
        Rect frame = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        // 状态栏高度
        int statusBarHeight = frame.top;
        View v = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
        int contentTop = v.getTop();
        // statusBarHeight是上面所求的状态栏的高度
        int titleBarHeight = contentTop - statusBarHeight;
        imageProcess.noequl("statusBarHeight=", statusBarHeight);
        imageProcess.noequl("titleBarHeight=", titleBarHeight);

        preferences = getSharedPreferences("set", MODE_PRIVATE);
        content = preferences.getString("content", "可预先在设置中设置短信内容");

        processUtil = new RemoveNoise();
        // service = new PreferencesService(this);
        // Map<String, String> params = service.getPreferences("content");
        // content = params.get("content");
        // content.setSelection(content.getText().length());
        // 截取并灰度化
        greyScreen();
        try {
            // do {
            // if (isWriteRecogize) {
            // // 二值化
            // binaryzation(1);
            // // 去除像素大的和非常小的噪音
            // removeMaxMinNoise(1);
            // recoginze_h();
            // } else {

            // 识别印刷体
            // 二值化
            binaryzation(0);
            // 去除像素大的和非常小的噪音
            removeMaxMinNoise(0);
            recoginze();
            // imageProcess.noequl("isWriteRecogize is true", 1);
            // }
            // } while (isOk);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /*
     * // 灰度化
     */
    public void greyScreen() {
        String path = Environment.getExternalStorageDirectory() + File.separator + "KuaiDiBangShou" + File.separator
                + "greyTemp.jpg";
        int degree = ImageThumbnail.getBitmapDegree(path);
        imageProcess.noequl(path, degree);
        // //取的原图
        Bitmap bitmap = BitmapFactory.decodeFile(path);
//		// //旋转
//		Bitmap bitmap1 = bitmap;
//		if (bitmap.getWidth() > bitmap.getHeight()) {
//			bitmap1 = ImageThumbnail.rotateBitmapByDegree(bitmap, 90);
//		}
//
//		// //截取
//		//
//		float scaleLeft = preferences.getFloat("positionLeft",  (float) 1.0 / 6);
//		float scaleTop = preferences.getFloat("positionTop", (float) 5.0 /24);
//		// float scaleHeight = bitmap1.getHeight() / mScreenHeight;
//		// float scaleWidth=bitmap.getWidth()/mScreenWidth;
//		imageProcess.noequl("scaleLeft ", scaleLeft);
//		imageProcess.noequl("scaleTop ", scaleTop);
//		// imageProcess.noequl("(int)( bitmap.getWidth()*scaleLeft = ",(int)(
//		// bitmap.getWidth()*scaleLeft));
//
//		Bitmap screenBitmap = Bitmap.createBitmap(bitmap1, (int)(bitmap.getWidth() * scaleLeft),
//				(int) (bitmap1.getHeight() *scaleTop), (int) (bitmap1.getWidth() * (1- 2*scaleLeft)),
//				(int) ((0.25-scaleTop ) * bitmap1.getHeight()*2));
//		imageProcess.noequl("getHeight()= ", ((scaleTop - 0.25) * bitmap1.getHeight()));


        imageProcess imageprocess = new imageProcess();
        // 转换为灰度图
        greyBitmap = imageprocess.RGBToGrey(bitmap);
        iv_photo.setImageBitmap(bitmap);
        width = greyBitmap.getWidth();
        height = greyBitmap.getHeight();

    }

    int px(float pix) {
        return (int) ((pix + 0.5) * 1.5);
    }

    /*
     * // 二值化
     */
    public void binaryzation(int id) {

        // 取出截取的灰度图
        pixels = new int[width * height];
        // 数组过大，内存溢出
        greyBitmap.getPixels(pixels, 0, width, 0, 0, width, height);
        by = new int[width * height];
        // int[] by1 = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            by[i] = (0xff & pixels[i]);
        }


    }

    /*
     * 去除非常大的和非常小的噪音，并按x排序
     */
    int count = 0;

    public void removeMaxMinNoise(int id) {
        // otsu二值化
        int thresholdValue = processUtil.byAndPixelsOtsu(by, pixels, width, height, 0, 0, width, height, id);
        //removeLine(id);
        int[] a = new int[700];


        //	processUtil.PreSegment_Line(by,width,height);
//TODO 排除水平垂直长线
        a = removeNoise(by, width, height);// 连通圆分割,不是去除噪音
        processUtil.initCount(a);
//TODO 去除大的连通元

        int by1[] = new int[width * height];
        for (int i = 0; i < width * height; i++) {
            by1[i] = by[i];
        }
        //行分割
        int lineCount = processUtil.PreSegment_Line_hxc(by1, width, height);
        if (lineCount == -1) {
            Toast.makeText(this, "没有检测到字符", Toast.LENGTH_LONG).show();
            return;
        }

        //根据过度行分割，确定所需要的连通元
        processUtil.RestoreCom(a, by1, width, height);
        //确定识别框,恢复同一个字符上下断开的，不在中间线上的部分
        processUtil.PreRecoginzeRect(a, width, height);
        //去除非常高的连通元
        processUtil.removeMaxNoise(a, width, height);

        //再次确定识别框
        Rect recongizeRect= processUtil.RecoginzeRect(a,width,height);




        //显示灰度图
        greyBitmap1 = Bitmap.createBitmap(width, height, Config.RGB_565);
        greyBitmap1.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap bitmap= drawRectangle(greyBitmap1,recongizeRect);
        Rect rect2=new Rect(0,processUtil.iTop,width,processUtil.iBottom);
        Bitmap bitmap2=drawRectangle(bitmap,rect2);
        iv_2.setImageBitmap(bitmap2);


//			//显示除去非中间行的像素
//			int numPixels[] = new int[width * height];
//			for (int p = 0; p < height; p++) {
//				for (int q = 0; q < width; q++) {
//					int gray = by1[p * width + q];
//					if (by1[p * width + q] > thresholdValue) {
//						gray = 225;
//						by1[p * width + q] = 255;
//					} else {
//						gray = 0;
//						by1[p * width + q] = 0;
//					}
//					int newcolor = (gray << 16) | (gray << 8) | (gray);
//					numPixels[p * width + q] = newcolor;
//				}
//			}
//		    Bitmap	greyBitmap = Bitmap.createBitmap(width, height, Config.RGB_565);
//			greyBitmap.setPixels(by1, 0, width, 0, 0, width, height);
//			iv_2.setImageBitmap(greyBitmap);

        sorta = new int[a.length];
        count = a.length / 7;
        processUtil.initSorta(a, sorta);

//		int tempHeight=processUtil.iBottom-processUtil.iTop;
//		if(tempHeight>20){
//			height=tempHeight;
//		}


        //合并同一个字符
        processUtil.CombainOneCharact(sorta, width, height);
        processUtil.CombainAgain(sorta, width, height);
//		processUtil.CombainOne_jing(sorta,width,height);
//		//TODO //对过大连通元重新进行处理
        //TODO 排除水平垂直长线
     processUtil.removeBasedOnBouttomTop(sorta, width, height, id);
      processUtil.initForwordAndBack(sorta);



    }

    private Bitmap drawRectangle(Bitmap greyBitmap1, Rect recongizeRect) {
        Bitmap bitmap=Bitmap.createBitmap(greyBitmap1);
        Canvas canvas=new Canvas(bitmap);
        Paint paint=new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        canvas.drawRect(recongizeRect,paint);


        return bitmap;
    }


	/*
     * 识别印刷体
	 */

    public void recoginze() throws IOException {
        ArrayList<Integer> alignHightVaule = new ArrayList<Integer>();
        GridLayout gridlaout = (GridLayout) findViewById(R.id.Linearproccess);
        // ArrayList<Integer> numList=new ArrayList<Integer>();
        // for (int iRecongize = 3; iRecongize < 4; iRecongize++) {

        for (int i = processUtil.forword; i <= processUtil.back; i++) {
//            	for (int i = 0; i <=count-1; i++) {
            if (sorta[i * 7] == 0) {
                int w = sorta[i * 7 + 2] - sorta[i * 7 + 1];
                int h = sorta[i * 7 + 4] - sorta[i * 7 + 3];
                float percent = (float) sorta[i * 7 + 5] / (float) w / (float) h;

                ImageView tempIamge = new ImageView(this);
                LayoutParams lp = new LayoutParams();
                lp.leftMargin = 4;
                if (greyBitmap1.getWidth() >= w + 1) {
                    w = w + 1;
                }
                if (greyBitmap1.getHeight() >= h + 1) {
                    h = h + 1;
                }
                Bitmap numBitmap1 = Bitmap.createBitmap(greyBitmap1, sorta[i * 7 + 1], sorta[i * 7 + 3], w, h);
                Bitmap numBitmap = ImageThumbnail.picScaleTo32(numBitmap1);
                tempIamge.setImageBitmap(numBitmap1);
                gridlaout.addView(tempIamge, lp);
                int numWidth = numBitmap.getWidth();
                int numHeight = numBitmap.getHeight();
                int[] numPixels = new int[numWidth * numHeight];
                // 数组过大，内存溢出
                numBitmap.getPixels(numPixels, 0, numWidth, 0, 0, numWidth, numHeight);
                int[] image = new int[numWidth * numHeight];
                // int[] by1 = new int[width * height];
                int pixButoom = 0;
                int pixH = 0;
                int pix3 = 0;
                int pix0 = 0;
                for (int rh = 0; rh < numHeight; rh++)
                    for (int rw = 0; rw < numWidth; rw++) {
                        int temp = (0xff & numPixels[rh * numWidth + rw]);
                        if (temp > 126) {
                            image[rh * numWidth + rw] = 0;
                        } else {
                            image[rh * numWidth + rw] = 255;
                            if (rh == numHeight - 2) {
                                pixButoom++;
                            }
                            if (rw == numWidth*2 / 3) {
                                pixH++;
                            }
                            if (rw == 2) {
                                pix3++;
                            }
                            if (rw > numWidth / 3 && rw < numWidth * 2 / 3) {
                                if (rh > numHeight / 3 && rh < numHeight * 2 / 3) {
                                    pix0++;
                                }
                            }
                        }
                    }


                int num = -1;
                float wAh = (float) numBitmap1.getWidth() / numBitmap1.getHeight();
//                if (wAh < 0.3 || ((percent == processUtil.averagePercent) && (wAh < 0.45))
//                        || ((pixButoom >= numWidth * 2 / 3) && (pixH >= numHeight - 3))) {
//                    num = 1;
//                    // Toast.makeText(this, "1", 1).show();
//                } else {

                    num = Recognize(image,numWidth,numHeight, 0);
                    if (percent > 0.55) {
                        if (num == 5) {
                            if (pix3 < numHeight / 2) {
                                num = 3;
                                // Toast.makeText(this, "not 5,is 3",
                                // 1).show();
                            }
                        }
                    }
                //}
                if (num == 7 || num == 10) {
                    if (pixH > numHeight * 3 / 4) {
                        num = 1;
                        // Toast.makeText(this, "no 7 ||拒识", 1).show();
                    }
                    // Toast.makeText(this, "no 7 ", num).show();
                    if (wAh < 0.45) {
                        num = 1;
                    }
                    // imageProcess.noequl("wah=", wAh);
                }
                if (num == 4) {
                    if (wAh < 0.45) {
                        num = 1;
                    }
                }
                if (num == 1) {
                    if (wAh > 0.6 && pixH < numHeight * 5 / 6) {
                        num = 7;
                    }
                }
                if (num == 8) {
                    if (pix0 < 10) {
                        num = 0;
                        // Toast.makeText(this, "not 8,is 0", 1).show();
                    }
                }
                alignHightVaule.add(num);
                // tv_num.setVisibility(View.VISIBLE);
                // if (num >= 10) {
                // tv_num.append("*");
                // } else {
                // tv_num.append(num + "");
                // }

            }
            if (tv_num.getText().toString().trim() == "") {
                Toast.makeText(this, "未识别出内容...", Toast.LENGTH_LONG).show();
            }
            // }
            // break;
            // }
        }
//		Iterator it = alignHeight.iterator();
//		int sumHeight = 0;
//		while (it.hasNext()) {
//			int temp = (Integer) it.next();
//			sumHeight += temp;
//		}
//		if(alignHeight.size()>0) {
//			int averNumHeight = sumHeight / alignHeight.size();
//			// imageProcess.noequl("averNumHeight= ", averNumHeight);
//			int subHeightCount = 0;
//			for (int i = alignHeight.size() - 1; i > 0; i--) {
//				int temp = (alignHeight.get(i) - alignHeight.get(i - 1)) * (alignHeight.get(i) - alignHeight.get(i - 1));
//				int tempRemove = (alignHeight.get(i) - averNumHeight) * (alignHeight.get(i) - averNumHeight);
//				// imageProcess.noequl("temp=", temp);
//				if (temp > 100) {
//					subHeightCount++;
//				}
//				if (tempRemove > 100) {
//					alignHeight.remove(i);
//					alignHightVaule.remove(i);
//				}
//			}
//		}


        isOk = false;
        tv_num.setVisibility(View.VISIBLE);
        int rejectNum = 0;
        for (int i = 0; i < alignHightVaule.size(); i++) {
            if (alignHightVaule.get(i) >= 10) {
                tv_num.append("*");
                if (rejectNum > 3) {
                    gridlaout.removeAllViews();
                    isOk = true;
                    isWriteRecogize = true;
                    // imageProcess.noequl("* *recogine again...", 1);
                }
            } else {
                tv_num.append(alignHightVaule.get(i) + "");
            }

        }
        // }

    }

    /*
     * 保存图片
     */
    public void savePic(View v) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + "KuaiDiBangShou" + File.separator
                + "ScreenPicture";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US);
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        final String fname = file + File.separator + sdf.format(new Date()) + ".png";
        View view = v.getRootView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        final Bitmap bitmap = view.getDrawingCache();
        // FileOutputStream out = null;
        if (bitmap != null) {
            try {
                FileOutputStream out;
                out = new FileOutputStream(fname);
                bitmap.compress(CompressFormat.PNG, 100, out);
                Toast.makeText(processActivity.this, "图片已保存", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                System.out.println("file not write!");
            }
        } else {
            System.out.println("bitmap is NULL!");
        }
    }

    /*
     * 把数据文件写入手机sd卡
     */
    private void assetsDataToSD(String filePath, String fileName) throws IOException {
        InputStream myInput;
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        // 判断sd卡是否存在
        if (sdCardExist) {
            sdDir = new File(filePath);// 获取根目录
        } else {
            Toast.makeText(this, "没有找到sd卡", Toast.LENGTH_LONG).show();
        }
        if (!sdDir.exists()) {
            sdDir.mkdirs();
        }
        // File dir_file=new
        // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),fileName);
        OutputStream myOutput = new FileOutputStream(sdDir + "//" + fileName);
        myInput = this.getAssets().open(fileName);
        byte[] buffer = new byte[1024];
        int length = myInput.read(buffer);
        while (length > 0) {
            myOutput.write(buffer, 0, length);
            length = myInput.read(buffer);
        }
        myOutput.flush();
        myInput.close();
        myOutput.close();
    }

    /*
     * 发送短信
     */
    public void sendMsg() {
        String number = tv_num.getText().toString();
        String context = content;
        // SmsManager manager=SmsManager.getDefault();
        // ArrayList<String> texts=manager.divideMessage(context);
        // for(String text:texts){
        // manager.sendTextMessage(number, null, text, null, null);
        // }
        // Toast.makeText(getApplicationContext(), "send success",1).show();
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse("smsto:" + number));
        sendIntent.putExtra("sms_body", context);
        startActivity(sendIntent);
    }

    /*
     * 打电话
     */
    public void callphone() {
        String number = tv_num.getText().toString();
        // Intent intent=new Intent();
        // intent.setAction("android.intent.action.CALL");
        // intent.addCategory("android.intent.category.DEFA")
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + number));
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

        this.finish();
        super.onBackPressed();
    }
}
