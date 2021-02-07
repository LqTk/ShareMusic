package tk.com.sharemusic.myview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import tk.com.sharemusic.R;

public class RoundAngleImageView extends androidx.appcompat.widget.AppCompatImageView {
    private int defaultRadius = 0;
    private int radius;
    private static final ScaleType SCALE_TYPE = ScaleType.CENTER_CROP;

    private float mBorderWidth = 0; // 边框大小,默认为3
    private int mBorderColor = Color.GRAY; // 边框颜色，默认为白色

    private float mBitmapRadius = 15; // 矩形的圆角半径,默认为3

    private Paint mBorderPaint = new Paint();

    private RectF mDrawableRect = new RectF(); // imageview的矩形区域
    private RectF mBorderRect = new RectF(); // 边框的矩形区域

    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final int COLORDRAWABLE_DIMENSION = 2;
    private ColorFilter mColorFilter;
    private Paint mBitmapPaint = new Paint();
    private BitmapShader mBitmapShader;
    private Bitmap mBitmap;
    private final Matrix mShaderMatrix = new Matrix();

    {
        mBorderPaint.setStyle(Paint.Style.FILL);
        mBorderPaint.setStrokeWidth(mBorderWidth);
        mBorderPaint.setColor(mBorderColor);
        mBorderPaint.setAntiAlias(true);
    }

    {
        mBitmapPaint.setAntiAlias(true);
    }

    private boolean mSetupPending;

    public RoundAngleImageView(Context context) {
        super(context);
        init(context,null);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public RoundAngleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs){
        if (Build.VERSION.SDK_INT<18){
            setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundAngleImageView);
        radius = array.getDimensionPixelOffset(R.styleable.RoundAngleImageView_radius,defaultRadius);
        mBorderWidth = array.getDimensionPixelOffset(R.styleable.RoundAngleImageView_border_width,defaultRadius);
        Log.d("borderWidth==",mBorderWidth+",radius="+radius);

        if (radius!=0){
            mBitmapRadius = radius;
        }

        array.recycle();

        this.setScaleType(SCALE_TYPE);

        if (mSetupPending){
            setUp();
            mSetupPending = false;
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 边框的矩形区域不能等于ImageView的矩形区域，否则边框的宽度只显示了一半
        if (mBorderWidth > 0) { // 绘制边框
            canvas.drawRoundRect(mBorderRect, mBitmapRadius, mBitmapRadius, mBorderPaint);
        }

        canvas.drawRoundRect(mDrawableRect, mBitmapRadius, mBitmapRadius, mBitmapPaint);

    }

    //　设置图片的绘制区域
    private void initRect() {

        mDrawableRect.top = 0;
        mDrawableRect.left = 0;
        mDrawableRect.right = getWidth(); // 宽度
        mDrawableRect.bottom = getHeight(); // 高度

        // 边框的矩形区域不能等于ImageView的矩形区域，否则边框的宽度只显示了一半
        mBorderRect.top = mBorderWidth / 2;
        mBorderRect.left = mBorderWidth / 2;
        mBorderRect.right = getWidth() - mBorderWidth / 2;
        mBorderRect.bottom = getHeight() - mBorderWidth / 2;
    }


    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        mBitmap = bm;
        setUp();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        mBitmap = getBitmapFormDrawable(drawable);
        setUp();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        mBitmap = getBitmapFormDrawable(getDrawable());
        setUp();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        mBitmap = getBitmapFormDrawable(getDrawable());
        setUp();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        if (cf == mColorFilter){
            return;
        }

        mColorFilter = cf;
        mBitmapPaint.setColorFilter(mColorFilter);
        invalidate();
    }

    private Bitmap getBitmapFormDrawable(Drawable drawable) {
        if (drawable==null){
            return null;
        }

        if (drawable instanceof BitmapDrawable){
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable){
                bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,COLORDRAWABLE_DIMENSION,BITMAP_CONFIG);
            }else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),drawable.getIntrinsicHeight(),BITMAP_CONFIG);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0,0,canvas.getWidth(),canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }catch (OutOfMemoryError e){
            return null;
        }
    }

    private void setUp(){
        // super(context, attrs, defStyle)调用setImageDrawable时,成员变量还未被正确初始化
        if (mBitmapPaint == null) {
            return;
        }
        if (mBitmap == null) {
            invalidate();
            return;
        }
        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint.setShader(mBitmapShader);

        // 固定为CENTER_CROP,使图片在ｖｉｅｗ中居中并裁剪
        mShaderMatrix.set(null);
        // 缩放到高或宽　与view的高或宽　匹配
        float scale = Math.max(getWidth() * 1f / mBitmap.getWidth(), getHeight() * 1f / mBitmap.getHeight());
        // 由于BitmapShader默认是从画布的左上角开始绘制，所以把其平移到画布中间，即居中
        float dx = (getWidth() - mBitmap.getWidth() * scale) / 2;
        float dy = (getHeight() - mBitmap.getHeight() * scale) / 2;
        mShaderMatrix.setScale(scale, scale);
        mShaderMatrix.postTranslate(dx, dy);
        mBitmapShader.setLocalMatrix(mShaderMatrix);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        initRect();
        setUp();
    }

}
