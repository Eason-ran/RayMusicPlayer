package com.raymondqk.raymusicplayer.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.raymondqk.raymusicplayer.R;

/**
 * Created by 陈其康 raymondchan on 2016/8/3 0003.
 */
public class AvatarCircle extends ImageView {

    private int mWidth;
    private int mRadius;
    private BitmapShader mBitmapShader;
    private Matrix mMatrix;
    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private float mStkroeWidth;
    private int mStrokeColor;


    public AvatarCircle(Context context) {
        super(context);
        inti(context, null);
    }

    public AvatarCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        inti(context, attrs);
    }

    public AvatarCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inti(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = (widthMeasureSpec>heightMeasureSpec)?heightMeasureSpec:widthMeasureSpec;
        setMeasuredDimension(mWidth,mWidth);
    }

    //设置图片着色器
    private void setBitmapShader() {
        //首先获得drawable对象，也就是控件属性的src，也就是我们的图片喇
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }
        //将图片转换为Bitmap
        Bitmap bitmap = drawableToBitmap(drawable);
        //use the bitmap to create a BitmapShader 将bitmap载入着色器，后面两个参数为x，y轴的缩放模式，CLAMP代表拉伸
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        // init a scale by float
        float scale = 1.0f;
        //get the bitmap's min one between its height and width
        int bitmapSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        //calculate the scale between bitmap and view
        mWidth = getWidth() - 100;
        scale = mWidth * 1.0f / bitmapSize;  // warn: use float to avoid some problem cause by precision


        //set the 变换矩阵 with scale
        mMatrix.setScale(scale, scale);//bitmapShader's width and height both set the scale
        mBitmapShader.setLocalMatrix(mMatrix);
        //use the shader to set the Paint
        mBitmapPaint.setAntiAlias(true); //消除锯齿
        mBitmapPaint.setShader(mBitmapShader);

        //after setting the paint,we go to draw our circle avatar


    }

    @Override
    protected void onDraw(Canvas canvas) {
        //        super.onDraw(canvas);
        if (getDrawable() == null) {
            return;
        }

        setBitmapShader();
        //        draw circle
        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - 30, mBitmapPaint);

        //draw border
        setBorderPaint();

        canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2 - 30, mBorderPaint);


    }

    /**
     * 将图片转换为Bitmap的工具函数
     * @param drawable
     * @return
     */
    private Bitmap drawableToBitmap(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    private void inti(Context context, AttributeSet attrs) {
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBorderPaint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AvatarCircle);
        mStkroeWidth = typedArray.getDimension(R.styleable.AvatarCircle_StrokeWidth, 0);
        mStrokeColor = typedArray.getColor(R.styleable.AvatarCircle_StrokeColor, Color.WHITE);
        typedArray.recycle();
    }

    private void setBorderPaint() {
        //set border paint
        mBorderPaint.setStyle(Paint.Style.STROKE);
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(mStrokeColor);
        mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        mBorderPaint.setStrokeWidth(mStkroeWidth);
        this.setLayerType(LAYER_TYPE_SOFTWARE, mBorderPaint);
        mBorderPaint.setShadowLayer(12.0f, 3.0f, 3.0f, Color.BLACK);

    }
}
