package cn.hxc.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

public class DashedLineView extends View {

	public DashedLineView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		Paint paint = new Paint();
		 paint.setStyle(Paint.Style.STROKE);
		 paint.setColor(-10066329);//��ɫ�����Լ�����
		 Path path = new Path();
		 path.moveTo(10, 0);//��ʼ����
		 path.lineTo(10,500);//�յ�����
		 PathEffect effects = new DashPathEffect(new float[]{8,8,8,8},1);//�������ߵļ���͵�ĳ���
		 paint.setPathEffect(effects);
		 canvas.drawPath(path, paint);		
	}
	public void drawDashedLine(float x){
		
	}
}
