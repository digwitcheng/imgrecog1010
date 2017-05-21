package cn.hxc.imgrecognition;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * Created by hxc on 2017/5/20.
 */

public class HXCButton extends FrameLayout {
    private Button btn;
    private TextView title;
    public HXCButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.hxc_button,this);
        title= (TextView) findViewById(R.id.title_tw);
        btn= (Button) findViewById(R.id.top_btn);
       // TypedArray attributes=context.obtainStyledAttributes(attrs,R.styleable.CustomTitleBar);

    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        super.setBackgroundDrawable(background);
    }
}
