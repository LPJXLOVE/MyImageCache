package com.example.lpjxlove.mytest.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.example.lpjxlove.mytest.R;

/**
 * Created by LPJXLOVE on 2016/6/19.
 */
public class ImageLint extends View {
    private Drawable background;
    private Paint BitmapPaint,BackgroundPaint;
    private Bitmap b;


    public ImageLint(Context context) {
        this(context,null);

    }

    public ImageLint(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public ImageLint(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray A=context.obtainStyledAttributes(attrs, R.styleable.ImageLint,defStyleAttr,0);
        int count=A.getIndexCount();
        for (int i=0;i<count;i++){
            int index=A.getIndex(i);
            switch (index){

                case R.styleable.ImageLint_background_:

                    background=A.getDrawable(index);



            }


        }
        A.recycle();





        init();
    }


    private PorterDuffXfermode p;
    private Rect r;
    private void init() {

        BitmapPaint=new Paint(Paint.ANTI_ALIAS_FLAG);
        BitmapPaint.setColor(Color.BLUE);
        BitmapPaint.setStyle(Paint.Style.FILL);
        p=new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
        b=Bitmap.createBitmap(background.getIntrinsicWidth(),background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        r=new Rect(0,0,background.getIntrinsicWidth(),background.getIntrinsicHeight());

    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth()/2,getHeight()/2,50,
                BitmapPaint);
        BitmapPaint.setXfermode(p);
       canvas.drawBitmap(b,getLeft(),getTop(),BitmapPaint);

    }
}
