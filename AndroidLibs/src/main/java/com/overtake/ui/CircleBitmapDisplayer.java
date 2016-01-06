package com.overtake.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.overtake.utils.OTLog;

/**
 * Created by kevinhoo on 14-3-19.
 */
public class CircleBitmapDisplayer implements BitmapDisplayer {

    private static final boolean DEBUG = true;

    @Override
    public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
        if (!(imageAware instanceof ImageViewAware)) {
            throw new IllegalArgumentException("ImageAware should wrap ImageView. ImageViewAware is expected.");
        }

        if (bitmap.getWidth() < imageAware.getWidth() || bitmap.getHeight() < imageAware.getHeight()) {
            Point size = findProperSize(bitmap, imageAware);
            bitmap = Bitmap.createScaledBitmap(bitmap, size.x, size.y, false);
        } else if (bitmap.getHeight() > imageAware.getHeight() || bitmap.getWidth() > imageAware.getWidth()) {

            if (DEBUG)
                OTLog.i(this, "bitmap:" + bitmap.getHeight() + ":" + bitmap.getWidth());
            if (DEBUG)
                OTLog.i(this, "imageAware:" + imageAware.getHeight() + ":" + imageAware.getWidth());

            Point size = findProperSize(bitmap, imageAware);

            if (DEBUG)
                OTLog.i(this, "found size:" + size);

            bitmap = Bitmap.createScaledBitmap(bitmap, size.x, size.y, false);
        }

        imageAware.setImageDrawable(new CircleDrawable(bitmap));
    }

    static Point findProperSize(Bitmap bitmap, ImageAware imageAware) {
        int awareMax = Math.max(imageAware.getWidth(), imageAware.getHeight());

        //x=width,y=height
        Point point = new Point(awareMax, awareMax);
        //----
        //|  |
        //|  |
        //----
        if (bitmap.getWidth() == 0 || bitmap.getHeight() == 0) {
            return point;
        }

        if (awareMax == 0) {
            int bitmapMax = Math.max(bitmap.getWidth(), bitmap.getHeight());
            return new Point(bitmapMax, bitmapMax);
        }

        //make the bitmap min to match the awaremax, and the other to scaled as ratio
        if (bitmap.getHeight() > bitmap.getWidth()) {
            float ratio = ((float) awareMax) / bitmap.getWidth();
            int height = (int) (ratio * bitmap.getHeight());
            point.y = height;
        } else {
            float ratio = ((float) awareMax / bitmap.getHeight());
            int width = (int) (ratio * bitmap.getWidth());
            point.x = width;
        }

        return point;
    }

    protected static class CircleDrawable extends Drawable {
        protected final BitmapShader mBitmapShader;
        protected final Paint mPaint;

        protected final RectF mRect = new RectF();

        CircleDrawable(Bitmap bitmap) {

            mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setShader(mBitmapShader);
        }

        @Override
        public void draw(Canvas canvas) {
            float min = Math.min(mRect.width(), mRect.height());
            canvas.drawCircle(mRect.centerX(), mRect.centerY(), min / 2, mPaint);
        }

        @Override
        protected void onBoundsChange(Rect bounds) {
            super.onBoundsChange(bounds);
            mRect.set(bounds);
        }

        @Override
        public void setAlpha(int alpha) {
            mPaint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            mPaint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }
    }
}
